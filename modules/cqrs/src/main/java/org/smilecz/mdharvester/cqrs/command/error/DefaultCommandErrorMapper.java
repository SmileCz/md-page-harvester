package org.smilecz.mdharvester.cqrs.command.error;

import org.smilecz.mdharvester.cqrs.command.Command;
import org.smilecz.mdharvester.cqrs.command.CommandContext;

public final class DefaultCommandErrorMapper implements CommandErrorMapper {

    @Override
    public RuntimeException map(Command command, CommandContext context, RuntimeException exception) {
        if (exception instanceof CommandDispatchException) {
            return exception;
        }
        return new CommandDispatchException(
                "Command "
                        + command.getClass().getName()
                        + " failed in attempt "
                        + context.attempt()
                        + ".",
                exception
        );
    }
}
