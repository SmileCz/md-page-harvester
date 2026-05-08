package org.smilecz.mdharvester.cqrs.command.store;

public final class CommandStoreException extends RuntimeException {

    public CommandStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
