package org.smilecz.mdharvester.cqrs.command.error;

public class CommandDispatchException extends RuntimeException {

    public CommandDispatchException(String message) {
        super(message);
    }

    public CommandDispatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
