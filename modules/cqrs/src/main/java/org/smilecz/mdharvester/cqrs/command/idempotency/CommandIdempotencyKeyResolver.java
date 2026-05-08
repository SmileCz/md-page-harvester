package org.smilecz.mdharvester.cqrs.command.idempotency;

import java.util.UUID;
import org.smilecz.mdharvester.cqrs.command.Command;
import org.smilecz.mdharvester.cqrs.command.CommandDispatchOptions;
import org.smilecz.mdharvester.cqrs.command.IdempotentCommand;

public final class CommandIdempotencyKeyResolver {

    public String resolve(Command command, CommandDispatchOptions options) {
        if (options.idempotencyKey() != null) {
            return options.idempotencyKey();
        }
        if (command instanceof IdempotentCommand idempotentCommand) {
            return idempotentCommand.idempotencyKey();
        }
        return command.getClass().getName() + ":" + UUID.randomUUID();
    }
}
