package org.smilecz.mdharvester.cqrs.query;

public interface QueryBus {

    <R> R ask(Query<R> query);
}
