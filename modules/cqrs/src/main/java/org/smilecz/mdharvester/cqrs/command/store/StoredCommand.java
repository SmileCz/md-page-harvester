package org.smilecz.mdharvester.cqrs.command.store;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import org.smilecz.mdharvester.cqrs.command.Command;
import org.smilecz.mdharvester.cqrs.command.CommandContext;

public record StoredCommand(
        UUID commandId,
        Command command,
        String correlationId,
        String principal,
        String idempotencyKey,
        Instant submittedAt,
        int attempts,
        CommandStatus status,
        String lastError,
        Instant nextAttemptAt,
        Instant completedAt
) implements Serializable {

    public CommandContext context() {
        return new CommandContext(
                commandId,
                correlationId,
                principal,
                idempotencyKey,
                submittedAt,
                attempts
        );
    }

    StoredCommand withStatus(CommandStatus nextStatus) {
        return new StoredCommand(
                commandId,
                command,
                correlationId,
                principal,
                idempotencyKey,
                submittedAt,
                attempts,
                nextStatus,
                lastError,
                nextAttemptAt,
                completedAt
        );
    }

    StoredCommand processing() {
        return new StoredCommand(
                commandId,
                command,
                correlationId,
                principal,
                idempotencyKey,
                submittedAt,
                attempts + 1,
                CommandStatus.PROCESSING,
                lastError,
                nextAttemptAt,
                completedAt
        );
    }

    StoredCommand succeeded(Instant completedAt) {
        return new StoredCommand(
                commandId,
                command,
                correlationId,
                principal,
                idempotencyKey,
                submittedAt,
                attempts,
                CommandStatus.SUCCEEDED,
                lastError,
                null,
                completedAt
        );
    }

    StoredCommand failed(String error, Instant nextAttemptAt) {
        return new StoredCommand(
                commandId,
                command,
                correlationId,
                principal,
                idempotencyKey,
                submittedAt,
                attempts,
                CommandStatus.FAILED,
                error,
                nextAttemptAt,
                null
        );
    }

    StoredCommand abandoned(String error, Instant completedAt) {
        return new StoredCommand(
                commandId,
                command,
                correlationId,
                principal,
                idempotencyKey,
                submittedAt,
                attempts,
                CommandStatus.ABANDONED,
                error,
                null,
                completedAt
        );
    }
}
