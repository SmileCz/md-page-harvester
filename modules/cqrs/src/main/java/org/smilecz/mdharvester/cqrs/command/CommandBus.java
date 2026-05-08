package org.smilecz.mdharvester.cqrs.command;

public interface CommandBus {

    void dispatch(Command command);
}
