package org.smilecz.mdharvester.cqrs.command;

import java.time.Instant;
import java.util.UUID;

public record CommandContext(
        UUID commandId,
        String correlationId,
        String principal,
        String idempotencyKey,
        Instant submittedAt,
        int attempt
) {
}
