package org.smilecz.mdharvester.commands;

public final class DuplicateCommandHandlerException extends RuntimeException {

    public DuplicateCommandHandlerException(Class<?> commandType) {
        super("Duplicate command handler for " + commandType.getName() + ".");
    }
}
