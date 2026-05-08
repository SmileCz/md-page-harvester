package org.smilecz.mdharvester.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

final class DefaultCommandBusTest {

    @Test
    void dispatchesCommandToRegisteredHandler() {
        DefaultCommandBus commandBus = new DefaultCommandBus(List.of(new TestCommandHandler("handled")));

        String result = commandBus.dispatch(new TestCommand("payload"));

        assertThat(result).isEqualTo("handled: payload");
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
                new TestCommandHandler("first"),
                new TestCommandHandler("second")
        )))
                .isInstanceOf(DuplicateCommandHandlerException.class)
                .hasMessage("Duplicate command handler for "
                        + TestCommand.class.getName()
                        + ".");
    }

    private record TestCommand(String value) implements Command<String> {
    }

    private record TestCommandHandler(String prefix) implements CommandHandler<TestCommand, String> {

        @Override
        public Class<TestCommand> commandType() {
            return TestCommand.class;
        }

        @Override
        public String handle(TestCommand command) {
            return prefix + ": " + command.value();
        }
    }
}
