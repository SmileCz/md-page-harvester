package org.smilecz.mdharvester.cqrs.command.audit;

import org.smilecz.mdharvester.cqrs.command.retry.RetryDecision;
import org.smilecz.mdharvester.cqrs.command.store.StoredCommand;

public interface CommandAuditListener {

    default void accepted(StoredCommand command) {
    }

    default void duplicateIgnored(StoredCommand command) {
    }

    default void started(StoredCommand command) {
    }

    default void succeeded(StoredCommand command) {
    }

    default void failed(StoredCommand command, RuntimeException exception, RetryDecision retryDecision) {
    }
}
