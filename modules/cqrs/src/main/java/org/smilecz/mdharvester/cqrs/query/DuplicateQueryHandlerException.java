package org.smilecz.mdharvester.cqrs.query;

public final class DuplicateQueryHandlerException extends RuntimeException {

    public DuplicateQueryHandlerException(Class<?> queryType) {
        super("Duplicate query handler for " + queryType.getName() + ".");
    }
}
