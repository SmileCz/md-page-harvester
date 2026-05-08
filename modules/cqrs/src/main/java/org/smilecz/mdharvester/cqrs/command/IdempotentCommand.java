package org.smilecz.mdharvester.cqrs.command;

public interface IdempotentCommand extends Command {

    String idempotencyKey();
}
