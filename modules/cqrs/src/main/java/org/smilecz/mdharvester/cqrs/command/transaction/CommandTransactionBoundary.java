package org.smilecz.mdharvester.cqrs.command.transaction;

public interface CommandTransactionBoundary {

    void execute(Runnable operation);
}
