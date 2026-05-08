package org.smilecz.mdharvester.cqrs.command;

public interface CommandHandler<C extends Command> {

    Class<C> commandType();

    void handle(C command);
}
