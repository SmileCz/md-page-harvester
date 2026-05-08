package org.smilecz.mdharvester.cqrs.command.authorization;

import org.smilecz.mdharvester.cqrs.command.Command;
import org.smilecz.mdharvester.cqrs.command.CommandContext;

public interface CommandAuthorizer<C extends Command> {

    Class<C> commandType();

    void authorize(C command, CommandContext context);
}
