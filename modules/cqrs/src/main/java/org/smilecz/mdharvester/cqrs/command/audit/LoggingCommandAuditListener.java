package org.smilecz.mdharvester.cqrs.command.audit;

import jakarta.inject.Singleton;
import org.smilecz.mdharvester.cqrs.command.retry.RetryDecision;
import org.smilecz.mdharvester.cqrs.command.store.StoredCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public final class LoggingCommandAuditListener implements CommandAuditListener {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingCommandAuditListener.class);

    @Override
    public void accepted(StoredCommand command) {
        LOG.info(
                "Accepted command {} id={} correlationId={} idempotencyKey={}",
                command.command().getClass().getName(),
                command.commandId(),
                command.correlationId(),
                command.idempotencyKey()
        );
    }

    @Override
    public void duplicateIgnored(StoredCommand command) {
        LOG.info(
                "Ignored duplicate command {} id={} idempotencyKey={} status={}",
                command.command().getClass().getName(),
                command.commandId(),
                command.idempotencyKey(),
                command.status()
        );
    }

    @Override
    public void started(StoredCommand command) {
        LOG.info(
                "Started command {} id={} attempt={}",
                command.command().getClass().getName(),
                command.commandId(),
                command.attempts()
        );
    }

    @Override
    public void succeeded(StoredCommand command) {
        LOG.info(
                "Succeeded command {} id={} attempts={}",
                command.command().getClass().getName(),
                command.commandId(),
                command.attempts()
        );
    }

    @Override
    public void failed(StoredCommand command, RuntimeException exception, RetryDecision retryDecision) {
        if (retryDecision.retry()) {
            LOG.warn(
                    "Command {} id={} attempt={} failed and will be retried after {}.",
                    command.command().getClass().getName(),
                    command.commandId(),
                    command.attempts(),
                    retryDecision.delay(),
                    exception
            );
            return;
        }
        LOG.error(
                "Command {} id={} attempt={} failed permanently.",
                command.command().getClass().getName(),
                command.commandId(),
                command.attempts(),
                exception
        );
    }
}
