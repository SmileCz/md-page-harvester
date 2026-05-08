package org.smilecz.mdharvester.cqrs.command.store;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import org.smilecz.mdharvester.cqrs.command.Command;
import org.smilecz.mdharvester.cqrs.command.CommandDispatchOptions;

public interface CommandStore {

    QueuedCommand enqueue(Command command, CommandDispatchOptions options, String idempotencyKey);

    Collection<StoredCommand> commandsReadyForProcessing(Instant now);

    StoredCommand markProcessing(UUID commandId);

    void markSucceeded(UUID commandId, Instant completedAt);

    void markFailed(UUID commandId, String error, Instant nextAttemptAt);

    void markAbandoned(UUID commandId, String error, Instant completedAt);
}
