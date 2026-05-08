package org.smilecz.mdharvester.cqrs.command.store;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import org.smilecz.mdharvester.cqrs.command.Command;
import org.smilecz.mdharvester.cqrs.command.CommandDispatchOptions;

@Singleton
public final class FileCommandStore extends InMemoryCommandStore {

    private final Path storeFile;

    public FileCommandStore(
            @Value("${md-page-harvester.command-bus.store-file:build/command-bus/commands.bin}") String storeFile
    ) {
        this.storeFile = Path.of(storeFile);
        load();
    }

    @Override
    public synchronized QueuedCommand enqueue(
            Command command,
            CommandDispatchOptions options,
            String idempotencyKey
    ) {
        QueuedCommand queuedCommand = super.enqueue(command, options, idempotencyKey);
        persist();
        return queuedCommand;
    }

    @Override
    public synchronized StoredCommand markProcessing(UUID commandId) {
        StoredCommand command = super.markProcessing(commandId);
        persist();
        return command;
    }

    @Override
    public synchronized void markSucceeded(UUID commandId, java.time.Instant completedAt) {
        super.markSucceeded(commandId, completedAt);
        persist();
    }

    @Override
    public synchronized void markFailed(UUID commandId, String error, java.time.Instant nextAttemptAt) {
        super.markFailed(commandId, error, nextAttemptAt);
        persist();
    }

    @Override
    public synchronized void markAbandoned(UUID commandId, String error, java.time.Instant completedAt) {
        super.markAbandoned(commandId, error, completedAt);
        persist();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!Files.exists(storeFile)) {
            return;
        }
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(storeFile))) {
            replaceWith((Map<UUID, StoredCommand>) input.readObject());
        } catch (IOException | ClassNotFoundException exception) {
            throw new CommandStoreException("Cannot load command store " + storeFile + ".", exception);
        }
    }

    private void persist() {
        try {
            Path parent = storeFile.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path temporaryFile = Files.createTempFile(parent, "commands-", ".bin");
            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(temporaryFile))) {
                output.writeObject(snapshot());
            }
            Files.move(
                    temporaryFile,
                    storeFile,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (IOException exception) {
            throw new CommandStoreException("Cannot persist command store " + storeFile + ".", exception);
        }
    }
}
