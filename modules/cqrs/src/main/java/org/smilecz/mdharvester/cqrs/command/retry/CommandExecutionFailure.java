package org.smilecz.mdharvester.cqrs.command.retry;

import org.smilecz.mdharvester.cqrs.command.Command;
import org.smilecz.mdharvester.cqrs.command.CommandContext;

public record CommandExecutionFailure(
        Command command,
        CommandContext context,
        RuntimeException exception
) {
}
