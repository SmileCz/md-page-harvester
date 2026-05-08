package org.smilecz.mdharvester.commands;

public interface CommandBus {

    <R> R dispatch(Command<R> command);
}
