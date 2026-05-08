package org.smilecz.mdharvester.cqrs.command.retry;

import java.time.Duration;
import java.util.Objects;

public record RetryDecision(boolean retry, Duration delay) {

    public static RetryDecision retryAfter(Duration delay) {
        return new RetryDecision(true, Objects.requireNonNull(delay, "delay"));
    }

    public static RetryDecision stop() {
        return new RetryDecision(false, Duration.ZERO);
    }
}
