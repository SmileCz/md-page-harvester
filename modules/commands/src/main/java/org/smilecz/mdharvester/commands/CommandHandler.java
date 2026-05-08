package org.smilecz.mdharvester.commands;

public interface CommandHandler<C extends Command<R>, R> {

    Class<C> commandType();

    R handle(C command);
}
