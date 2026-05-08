package org.smilecz.mdharvester.cqrs.command.retry;

import java.time.Duration;

public final class DefaultRetryPolicy implements RetryPolicy {

    private final int maxAttempts;
    private final Duration baseDelay;

    public DefaultRetryPolicy() {
        this(3, Duration.ofSeconds(30));
    }

    public DefaultRetryPolicy(int maxAttempts, Duration baseDelay) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be greater than zero.");
        }
        this.maxAttempts = maxAttempts;
        this.baseDelay = baseDelay;
    }

    @Override
    public RetryDecision decide(CommandExecutionFailure failure) {
        if (failure.context().attempt() >= maxAttempts) {
            return RetryDecision.stop();
        }
        return RetryDecision.retryAfter(baseDelay.multipliedBy(failure.context().attempt()));
    }
}
