package org.smilecz.mdharvester.cqrs.command.store;

public enum CommandStatus {
    PENDING,
    PROCESSING,
    SUCCEEDED,
    FAILED,
    ABANDONED
}
