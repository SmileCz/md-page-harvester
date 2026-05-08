package org.smilecz.mdharvester.cqrs.command.bus;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.smilecz.mdharvester.cqrs.command.Command;
import org.smilecz.mdharvester.cqrs.command.CommandBus;
import org.smilecz.mdharvester.cqrs.command.CommandContext;
import org.smilecz.mdharvester.cqrs.command.CommandDispatchOptions;
import org.smilecz.mdharvester.cqrs.command.CommandHandler;
import org.smilecz.mdharvester.cqrs.command.audit.CommandAuditListener;
import org.smilecz.mdharvester.cqrs.command.authorization.CommandAuthorizer;
import org.smilecz.mdharvester.cqrs.command.error.CommandErrorMapper;
import org.smilecz.mdharvester.cqrs.command.error.DefaultCommandErrorMapper;
import org.smilecz.mdharvester.cqrs.command.idempotency.CommandIdempotencyKeyResolver;
import org.smilecz.mdharvester.cqrs.command.retry.CommandExecutionFailure;
import org.smilecz.mdharvester.cqrs.command.retry.DefaultRetryPolicy;
import org.smilecz.mdharvester.cqrs.command.retry.RetryDecision;
import org.smilecz.mdharvester.cqrs.command.retry.RetryPolicy;
import org.smilecz.mdharvester.cqrs.command.store.CommandStore;
import org.smilecz.mdharvester.cqrs.command.store.InMemoryCommandStore;
import org.smilecz.mdharvester.cqrs.command.store.QueuedCommand;
import org.smilecz.mdharvester.cqrs.command.store.StoredCommand;
import org.smilecz.mdharvester.cqrs.command.transaction.CommandTransactionBoundary;
import org.smilecz.mdharvester.cqrs.command.transaction.NoopCommandTransactionBoundary;
import org.smilecz.mdharvester.cqrs.command.validation.CommandValidator;

@Singleton
public final class DefaultCommandBus implements CommandBus {

    private final Map<Class<?>, CommandHandler<?>> handlers;
    private final Collection<CommandValidator<?>> validators;
    private final Collection<CommandAuthorizer<?>> authorizers;
    private final Collection<CommandAuditListener> auditListeners;
    private final CommandStore commandStore;
    private final RetryPolicy retryPolicy;
    private final CommandTransactionBoundary transactionBoundary;
    private final CommandErrorMapper errorMapper;
    private final CommandIdempotencyKeyResolver idempotencyKeyResolver;

    public DefaultCommandBus(Collection<CommandHandler<?>> handlers) {
        this(
                handlers,
                List.of(),
                List.of(),
                List.of(),
                new InMemoryCommandStore(),
                new DefaultRetryPolicy(),
                new NoopCommandTransactionBoundary(),
                new DefaultCommandErrorMapper()
        );
    }

    @Inject
    public DefaultCommandBus(
            Collection<CommandHandler<?>> handlers,
            Collection<CommandValidator<?>> validators,
            Collection<CommandAuthorizer<?>> authorizers,
            Collection<CommandAuditListener> auditListeners,
            @Nullable CommandStore commandStore,
            @Nullable RetryPolicy retryPolicy,
            @Nullable CommandTransactionBoundary transactionBoundary,
            @Nullable CommandErrorMapper errorMapper
    ) {
        this.handlers = Map.copyOf(indexHandlers(handlers));
        this.validators = List.copyOf(validators);
        this.authorizers = List.copyOf(authorizers);
        this.auditListeners = List.copyOf(auditListeners);
        this.commandStore = commandStore == null ? new InMemoryCommandStore() : commandStore;
        this.retryPolicy = retryPolicy == null ? new DefaultRetryPolicy() : retryPolicy;
        this.transactionBoundary = transactionBoundary == null
                ? new NoopCommandTransactionBoundary()
                : transactionBoundary;
        this.errorMapper = errorMapper == null ? new DefaultCommandErrorMapper() : errorMapper;
        this.idempotencyKeyResolver = new CommandIdempotencyKeyResolver();
    }

    @Override
    public void dispatch(Command command) {
        dispatch(command, CommandDispatchOptions.defaults());
    }

    @Override
    public void dispatch(Command command, CommandDispatchOptions options) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(options, "options");

        validate(command);
        authorize(command, new CommandContext(
                null,
                options.correlationId(),
                options.principal(),
                options.idempotencyKey(),
                Instant.now(),
                0
        ));
        handlerFor(command.getClass());

        QueuedCommand queuedCommand = commandStore.enqueue(
                command,
                options,
                idempotencyKeyResolver.resolve(command, options)
        );
        if (queuedCommand.duplicate()) {
            auditListeners.forEach(listener -> listener.duplicateIgnored(queuedCommand.command()));
            return;
        }

        auditListeners.forEach(listener -> listener.accepted(queuedCommand.command()));
        process(queuedCommand.command(), true);
    }

    @Override
    public int recoverPendingCommands() {
        int processed = 0;
        for (StoredCommand command : commandStore.commandsReadyForProcessing(Instant.now())) {
            process(command, false);
            processed++;
        }
        return processed;
    }

    private static Map<Class<?>, CommandHandler<?>> indexHandlers(Collection<CommandHandler<?>> handlers) {
        Map<Class<?>, CommandHandler<?>> indexedHandlers = new HashMap<>();
        for (CommandHandler<?> handler : handlers) {
            CommandHandler<?> previousHandler = indexedHandlers.put(handler.commandType(), handler);
            if (previousHandler != null) {
                throw new DuplicateCommandHandlerException(handler.commandType());
            }
        }
        return indexedHandlers;
    }

    private void process(StoredCommand command, boolean throwMappedException) {
        StoredCommand processingCommand = commandStore.markProcessing(command.commandId());
        CommandContext context = processingCommand.context();
        try {
            validate(processingCommand.command());
            authorize(processingCommand.command(), context);
            auditListeners.forEach(listener -> listener.started(processingCommand));
            CommandHandler<Command> handler = handlerFor(processingCommand.command().getClass());
            transactionBoundary.execute(() -> handler.handle(processingCommand.command()));
            commandStore.markSucceeded(processingCommand.commandId(), Instant.now());
            auditListeners.forEach(listener -> listener.succeeded(processingCommand));
        } catch (RuntimeException exception) {
            RuntimeException mappedException = errorMapper.map(processingCommand.command(), context, exception);
            RetryDecision retryDecision = retryPolicy.decide(new CommandExecutionFailure(
                    processingCommand.command(),
                    context,
                    mappedException
            ));
            if (retryDecision.retry()) {
                commandStore.markFailed(
                        processingCommand.commandId(),
                        mappedException.getMessage(),
                        Instant.now().plus(retryDecision.delay())
                );
            } else {
                commandStore.markAbandoned(
                        processingCommand.commandId(),
                        mappedException.getMessage(),
                        Instant.now()
                );
            }
            auditListeners.forEach(listener -> listener.failed(processingCommand, mappedException, retryDecision));
            if (throwMappedException) {
                throw mappedException;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void validate(Command command) {
        for (CommandValidator<?> validator : validators) {
            if (validator.commandType().equals(command.getClass())) {
                ((CommandValidator<Command>) validator).validate(command);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void authorize(Command command, CommandContext context) {
        for (CommandAuthorizer<?> authorizer : authorizers) {
            if (authorizer.commandType().equals(command.getClass())) {
                ((CommandAuthorizer<Command>) authorizer).authorize(command, context);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private CommandHandler<Command> handlerFor(Class<?> commandType) {
        CommandHandler<?> handler = handlers.get(commandType);
        if (handler == null) {
            throw new NoCommandHandlerException(commandType);
        }
        return (CommandHandler<Command>) handler;
    }
}
