package org.smilecz.mdharvester.cqrs.command.validation;

import org.smilecz.mdharvester.cqrs.command.Command;

public interface CommandValidator<C extends Command> {

    Class<C> commandType();

    void validate(C command);
}
