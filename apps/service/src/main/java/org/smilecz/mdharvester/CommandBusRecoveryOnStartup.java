package org.smilecz.mdharvester;

import io.micronaut.context.annotation.Context;
import jakarta.annotation.PostConstruct;
import org.smilecz.mdharvester.cqrs.command.CommandBus;

@Context
final class CommandBusRecoveryOnStartup {

    private final CommandBus commandBus;

    CommandBusRecoveryOnStartup(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @PostConstruct
    void recoverPendingCommands() {
        commandBus.recoverPendingCommands();
    }
}
