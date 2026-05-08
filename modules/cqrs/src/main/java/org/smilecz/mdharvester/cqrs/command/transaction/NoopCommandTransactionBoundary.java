package org.smilecz.mdharvester.cqrs.command.transaction;

public final class NoopCommandTransactionBoundary implements CommandTransactionBoundary {

    @Override
    public void execute(Runnable operation) {
        operation.run();
    }
}
