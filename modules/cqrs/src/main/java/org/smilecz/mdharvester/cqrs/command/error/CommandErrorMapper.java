package org.smilecz.mdharvester.cqrs.command.error;

import org.smilecz.mdharvester.cqrs.command.Command;
import org.smilecz.mdharvester.cqrs.command.CommandContext;

public interface CommandErrorMapper {

    RuntimeException map(Command command, CommandContext context, RuntimeException exception);
}
