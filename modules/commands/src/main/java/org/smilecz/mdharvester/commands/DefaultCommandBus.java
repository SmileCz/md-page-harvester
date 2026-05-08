package org.smilecz.mdharvester.commands;

import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Singleton
public final class DefaultCommandBus implements CommandBus {

    private final Map<Class<?>, CommandHandler<?, ?>> handlers;

    public DefaultCommandBus(Collection<CommandHandler<?, ?>> handlers) {
        this.handlers = Map.copyOf(indexHandlers(handlers));
    }

    @Override
    public <R> R dispatch(Command<R> command) {
        Objects.requireNonNull(command, "command");
        CommandHandler<Command<R>, R> handler = handlerFor(command.getClass());
        return handler.handle(command);
    }

    private static Map<Class<?>, CommandHandler<?, ?>> indexHandlers(Collection<CommandHandler<?, ?>> handlers) {
        Map<Class<?>, CommandHandler<?, ?>> indexedHandlers = new HashMap<>();
        for (CommandHandler<?, ?> handler : handlers) {
            CommandHandler<?, ?> previousHandler = indexedHandlers.put(handler.commandType(), handler);
            if (previousHandler != null) {
                throw new DuplicateCommandHandlerException(handler.commandType());
            }
        }
        return indexedHandlers;
    }

    @SuppressWarnings("unchecked")
    private <R> CommandHandler<Command<R>, R> handlerFor(Class<?> commandType) {
        CommandHandler<?, ?> handler = handlers.get(commandType);
        if (handler == null) {
            throw new NoCommandHandlerException(commandType);
        }
        return (CommandHandler<Command<R>, R>) handler;
    }
}
