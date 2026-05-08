package org.smilecz.mdharvester.cqrs.command;

public final class DuplicateCommandHandlerException extends RuntimeException {

    public DuplicateCommandHandlerException(Class<?> commandType) {
        super("Duplicate command handler for " + commandType.getName() + ".");
    }
}
