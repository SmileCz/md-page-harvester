package org.smilecz.mdharvester.cqrs.query;

import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Singleton
public final class DefaultQueryBus implements QueryBus {

    private final Map<Class<?>, QueryHandler<?, ?>> handlers;

    public DefaultQueryBus(Collection<QueryHandler<?, ?>> handlers) {
        this.handlers = Map.copyOf(indexHandlers(handlers));
    }

    @Override
    public <R> R ask(Query<R> query) {
        Objects.requireNonNull(query, "query");
        QueryHandler<Query<R>, R> handler = handlerFor(query.getClass());
        return handler.handle(query);
    }

    private static Map<Class<?>, QueryHandler<?, ?>> indexHandlers(Collection<QueryHandler<?, ?>> handlers) {
        Map<Class<?>, QueryHandler<?, ?>> indexedHandlers = new HashMap<>();
        for (QueryHandler<?, ?> handler : handlers) {
            QueryHandler<?, ?> previousHandler = indexedHandlers.put(handler.queryType(), handler);
            if (previousHandler != null) {
                throw new DuplicateQueryHandlerException(handler.queryType());
            }
        }
        return indexedHandlers;
    }

    @SuppressWarnings("unchecked")
    private <R> QueryHandler<Query<R>, R> handlerFor(Class<?> queryType) {
        QueryHandler<?, ?> handler = handlers.get(queryType);
        if (handler == null) {
            throw new NoQueryHandlerException(queryType);
        }
        return (QueryHandler<Query<R>, R>) handler;
    }
}
