package org.smilecz.mdharvester.cqrs.command.retry;

public interface RetryPolicy {

    RetryDecision decide(CommandExecutionFailure failure);
}
