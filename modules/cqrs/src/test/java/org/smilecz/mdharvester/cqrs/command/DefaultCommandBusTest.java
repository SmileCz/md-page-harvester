package org.smilecz.mdharvester.cqrs.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.smilecz.mdharvester.cqrs.command.authorization.CommandAuthorizationException;
import org.smilecz.mdharvester.cqrs.command.authorization.CommandAuthorizer;
import org.smilecz.mdharvester.cqrs.command.bus.DefaultCommandBus;
import org.smilecz.mdharvester.cqrs.command.bus.DuplicateCommandHandlerException;
import org.smilecz.mdharvester.cqrs.command.bus.NoCommandHandlerException;
import org.smilecz.mdharvester.cqrs.command.error.CommandDispatchException;
import org.smilecz.mdharvester.cqrs.command.error.DefaultCommandErrorMapper;
import org.smilecz.mdharvester.cqrs.command.retry.CommandExecutionFailure;
import org.smilecz.mdharvester.cqrs.command.retry.RetryDecision;
import org.smilecz.mdharvester.cqrs.command.retry.RetryPolicy;
import org.smilecz.mdharvester.cqrs.command.store.CommandStatus;
import org.smilecz.mdharvester.cqrs.command.store.CommandStore;
import org.smilecz.mdharvester.cqrs.command.store.InMemoryCommandStore;
import org.smilecz.mdharvester.cqrs.command.store.StoredCommand;
import org.smilecz.mdharvester.cqrs.command.transaction.NoopCommandTransactionBoundary;
import org.smilecz.mdharvester.cqrs.command.validation.CommandValidationException;
import org.smilecz.mdharvester.cqrs.command.validation.CommandValidator;

final class DefaultCommandBusTest {

    @Test
    void dispatchesCommandToRegisteredHandlerWithoutReturningData() {
        TestCommandHandler handler = new TestCommandHandler();
        DefaultCommandBus commandBus = new DefaultCommandBus(List.of(handler));

        commandBus.dispatch(new TestCommand("payload"));

        assertThat(handler.handledValue).isEqualTo("payload");
    }

    @Test
    void rejectsMissingHandler() {
        DefaultCommandBus commandBus = new DefaultCommandBus(List.of());

        assertThatThrownBy(() -> commandBus.dispatch(new TestCommand("payload")))
                .isInstanceOf(NoCommandHandlerException.class)
                .hasMessage("No command handler registered for "
                        + TestCommand.class.getName()
                        + ".");
    }

    @Test
    void rejectsDuplicateHandlers() {
        assertThatThrownBy(() -> new DefaultCommandBus(List.of(
                new TestCommandHandler(),
                new TestCommandHandler()
        )))
                .isInstanceOf(DuplicateCommandHandlerException.class)
                .hasMessage("Duplicate command handler for "
                        + TestCommand.class.getName()
                        + ".");
    }

    @Test
    void storesCommandBeforeHandlingIt() {
        TestCommandHandler handler = new TestCommandHandler();
        TestCommandStore store = new TestCommandStore();
        DefaultCommandBus commandBus = commandBus(handler, store, List.of(), List.of(), new StopRetryPolicy());

        commandBus.dispatch(new TestCommand("payload"));

        StoredCommand storedCommand = store.snapshot().values().iterator().next();
        assertThat(storedCommand.command()).isEqualTo(new TestCommand("payload"));
        assertThat(storedCommand.status()).isEqualTo(CommandStatus.SUCCEEDED);
    }

    @Test
    void ignoresDuplicateCommandByIdempotencyKey() {
        TestCommandHandler handler = new TestCommandHandler();
        DefaultCommandBus commandBus = commandBus(
                handler,
                new TestCommandStore(),
                List.of(),
                List.of(),
                new StopRetryPolicy()
        );
        CommandDispatchOptions options = CommandDispatchOptions.builder()
                .idempotencyKey("download-page")
                .build();

        commandBus.dispatch(new TestCommand("payload"), options);
        commandBus.dispatch(new TestCommand("payload"), options);

        assertThat(handler.handledValues).containsExactly("payload");
    }

    @Test
    void treatsCommandsWithoutIdempotencyKeyAsSeparateIntents() {
        TestCommandHandler handler = new TestCommandHandler();
        DefaultCommandBus commandBus = commandBus(
                handler,
                new TestCommandStore(),
                List.of(),
                List.of(),
                new StopRetryPolicy()
        );

        commandBus.dispatch(new TestCommand("payload"));
        commandBus.dispatch(new TestCommand("payload"));

        assertThat(handler.handledValues).containsExactly("payload", "payload");
    }

    @Test
    void appliesValidationBeforeCommandIsStored() {
        TestCommandStore store = new TestCommandStore();
        DefaultCommandBus commandBus = commandBus(
                new TestCommandHandler(),
                store,
                List.of(new RejectingValidator()),
                List.of(),
                new StopRetryPolicy()
        );

        assertThatThrownBy(() -> commandBus.dispatch(new TestCommand("payload")))
                .isInstanceOf(CommandValidationException.class)
                .hasMessage("Invalid test command.");
        assertThat(store.snapshot()).isEmpty();
    }

    @Test
    void appliesAuthorizationBeforeCommandIsStored() {
        TestCommandStore store = new TestCommandStore();
        DefaultCommandBus commandBus = commandBus(
                new TestCommandHandler(),
                store,
                List.of(),
                List.of(new RejectingAuthorizer()),
                new StopRetryPolicy()
        );

        assertThatThrownBy(() -> commandBus.dispatch(new TestCommand("payload")))
                .isInstanceOf(CommandAuthorizationException.class)
                .hasMessage("Forbidden test command.");
        assertThat(store.snapshot()).isEmpty();
    }

    @Test
    void keepsFailedCommandForRetryAndRecoversItLater() {
        FlakyCommandHandler handler = new FlakyCommandHandler();
        TestCommandStore store = new TestCommandStore();
        DefaultCommandBus firstBus = commandBus(handler, store, List.of(), List.of(), new ImmediateRetryPolicy());

        assertThatThrownBy(() -> firstBus.dispatch(new TestCommand("payload")))
                .isInstanceOf(CommandDispatchException.class);
        assertThat(store.snapshot().values())
                .extracting(StoredCommand::status)
                .containsExactly(CommandStatus.FAILED);

        DefaultCommandBus recoveredBus = commandBus(handler, store, List.of(), List.of(), new ImmediateRetryPolicy());
        assertThat(recoveredBus.recoverPendingCommands()).isEqualTo(1);

        assertThat(handler.handledValues).containsExactly("payload", "payload");
        assertThat(store.snapshot().values())
                .extracting(StoredCommand::status)
                .containsExactly(CommandStatus.SUCCEEDED);
    }

    private static DefaultCommandBus commandBus(
            CommandHandler<?> handler,
            CommandStore store,
            List<CommandValidator<?>> validators,
            List<CommandAuthorizer<?>> authorizers,
            RetryPolicy retryPolicy
    ) {
        return new DefaultCommandBus(
                List.of(handler),
                validators,
                authorizers,
                List.of(),
                store,
                retryPolicy,
                new NoopCommandTransactionBoundary(),
                new DefaultCommandErrorMapper()
        );
    }

    private record TestCommand(String value) implements Command {
    }

    private static final class TestCommandHandler implements CommandHandler<TestCommand> {

        private String handledValue;
        private final List<String> handledValues = new ArrayList<>();

        @Override
        public Class<TestCommand> commandType() {
            return TestCommand.class;
        }

        @Override
        public void handle(TestCommand command) {
            handledValue = command.value();
            handledValues.add(command.value());
        }
    }

    private static final class FlakyCommandHandler implements CommandHandler<TestCommand> {

        private int failures = 1;
        private final List<String> handledValues = new ArrayList<>();

        @Override
        public Class<TestCommand> commandType() {
            return TestCommand.class;
        }

        @Override
        public void handle(TestCommand command) {
            handledValues.add(command.value());
            if (failures > 0) {
                failures--;
                throw new IllegalStateException("Temporary failure.");
            }
        }
    }

    private static final class RejectingValidator implements CommandValidator<TestCommand> {

        @Override
        public Class<TestCommand> commandType() {
            return TestCommand.class;
        }

        @Override
        public void validate(TestCommand command) {
            throw new CommandValidationException("Invalid test command.");
        }
    }

    private static final class RejectingAuthorizer implements CommandAuthorizer<TestCommand> {

        @Override
        public Class<TestCommand> commandType() {
            return TestCommand.class;
        }

        @Override
        public void authorize(TestCommand command, CommandContext context) {
            throw new CommandAuthorizationException("Forbidden test command.");
        }
    }

    private static final class ImmediateRetryPolicy implements RetryPolicy {

        @Override
        public RetryDecision decide(CommandExecutionFailure failure) {
            if (failure.context().attempt() >= 2) {
                return RetryDecision.stop();
            }
            return RetryDecision.retryAfter(Duration.ZERO);
        }
    }

    private static final class StopRetryPolicy implements RetryPolicy {

        @Override
        public RetryDecision decide(CommandExecutionFailure failure) {
            return RetryDecision.stop();
        }
    }

    private static final class TestCommandStore extends InMemoryCommandStore {

        @Override
        public Map<UUID, StoredCommand> snapshot() {
            return super.snapshot();
        }
    }
}
