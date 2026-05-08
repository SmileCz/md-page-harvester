package org.smilecz.mdharvester.cqrs.query;

public final class NoQueryHandlerException extends RuntimeException {

    public NoQueryHandlerException(Class<?> queryType) {
        super("No query handler registered for " + queryType.getName() + ".");
    }
}
