package org.smilecz.mdharvester.cqrs.command.validation;

import org.smilecz.mdharvester.cqrs.command.error.CommandDispatchException;

public final class CommandValidationException extends CommandDispatchException {

    public CommandValidationException(String message) {
        super(message);
    }
}
