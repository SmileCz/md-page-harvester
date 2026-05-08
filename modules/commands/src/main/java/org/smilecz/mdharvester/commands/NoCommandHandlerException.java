package org.smilecz.mdharvester.commands;

public final class NoCommandHandlerException extends RuntimeException {

    public NoCommandHandlerException(Class<?> commandType) {
        super("No command handler registered for " + commandType.getName() + ".");
    }
}
