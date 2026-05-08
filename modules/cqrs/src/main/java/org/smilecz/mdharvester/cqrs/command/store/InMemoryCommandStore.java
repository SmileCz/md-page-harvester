package org.smilecz.mdharvester.cqrs.command.store;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.smilecz.mdharvester.cqrs.command.Command;
import org.smilecz.mdharvester.cqrs.command.CommandDispatchOptions;

public class InMemoryCommandStore implements CommandStore {

    private final Map<UUID, StoredCommand> commands = new LinkedHashMap<>();

    @Override
    public synchronized QueuedCommand enqueue(
            Command command,
            CommandDispatchOptions options,
            String idempotencyKey
    ) {
        StoredCommand duplicate = findByIdempotencyKey(idempotencyKey);
        if (duplicate != null) {
            return new QueuedCommand(duplicate, true);
        }

        StoredCommand storedCommand = new StoredCommand(
                UUID.randomUUID(),
                command,
                options.correlationId(),
                options.principal(),
                idempotencyKey,
                Instant.now(),
                0,
                CommandStatus.PENDING,
                null,
                Instant.now(),
                null
        );
        commands.put(storedCommand.commandId(), storedCommand);
        return new QueuedCommand(storedCommand, false);
    }

    @Override
    public synchronized Collection<StoredCommand> commandsReadyForProcessing(Instant now) {
        return commands.values().stream()
                .filter(command -> command.status() == CommandStatus.PENDING
                        || command.status() == CommandStatus.PROCESSING
                        || command.status() == CommandStatus.FAILED)
                .filter(command -> command.nextAttemptAt() == null || !command.nextAttemptAt().isAfter(now))
                .toList();
    }

    @Override
    public synchronized StoredCommand markProcessing(UUID commandId) {
        StoredCommand command = requireCommand(commandId).processing();
        commands.put(commandId, command);
        return command;
    }

    @Override
    public synchronized void markSucceeded(UUID commandId, Instant completedAt) {
        commands.put(commandId, requireCommand(commandId).succeeded(completedAt));
    }

    @Override
    public synchronized void markFailed(UUID commandId, String error, Instant nextAttemptAt) {
        commands.put(commandId, requireCommand(commandId).failed(error, nextAttemptAt));
    }

    @Override
    public synchronized void markAbandoned(UUID commandId, String error, Instant completedAt) {
        commands.put(commandId, requireCommand(commandId).abandoned(error, completedAt));
    }

    protected synchronized Map<UUID, StoredCommand> snapshot() {
        return Map.copyOf(commands);
    }

    protected synchronized void replaceWith(Map<UUID, StoredCommand> nextCommands) {
        commands.clear();
        commands.putAll(nextCommands);
    }

    private StoredCommand findByIdempotencyKey(String idempotencyKey) {
        return commands.values().stream()
                .filter(command -> command.idempotencyKey().equals(idempotencyKey))
                .findFirst()
                .orElse(null);
    }

    private StoredCommand requireCommand(UUID commandId) {
        StoredCommand command = commands.get(commandId);
        if (command == null) {
            throw new IllegalArgumentException("Unknown command " + commandId + ".");
        }
        return command;
    }
}
