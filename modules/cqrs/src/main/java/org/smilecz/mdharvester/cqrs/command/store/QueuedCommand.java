package org.smilecz.mdharvester.cqrs.command.store;

public record QueuedCommand(StoredCommand command, boolean duplicate) {
}
