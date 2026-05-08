package org.smilecz.mdharvester.cqrs.command;

public final class NoCommandHandlerException extends RuntimeException {

    public NoCommandHandlerException(Class<?> commandType) {
        super("No command handler registered for " + commandType.getName() + ".");
    }
}
