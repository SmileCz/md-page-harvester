package org.smilecz.mdharvester.cqrs.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

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

    private record TestCommand(String value) implements Command {
    }

    private static final class TestCommandHandler implements CommandHandler<TestCommand> {

        private String handledValue;

        @Override
        public Class<TestCommand> commandType() {
            return TestCommand.class;
        }

        @Override
        public void handle(TestCommand command) {
            handledValue = command.value();
        }
    }
}
