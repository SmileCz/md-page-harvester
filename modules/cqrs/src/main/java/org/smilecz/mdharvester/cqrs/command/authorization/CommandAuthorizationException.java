package org.smilecz.mdharvester.cqrs.command.authorization;

import org.smilecz.mdharvester.cqrs.command.error.CommandDispatchException;

public final class CommandAuthorizationException extends CommandDispatchException {

    public CommandAuthorizationException(String message) {
        super(message);
    }
}
