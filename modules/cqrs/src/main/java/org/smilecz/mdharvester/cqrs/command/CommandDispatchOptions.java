package org.smilecz.mdharvester.cqrs.command;

import java.util.Objects;

public final class CommandDispatchOptions {

    private static final CommandDispatchOptions DEFAULTS = builder().build();

    private final String correlationId;
    private final String principal;
    private final String idempotencyKey;

    private CommandDispatchOptions(Builder builder) {
        this.correlationId = builder.correlationId;
        this.principal = builder.principal;
        this.idempotencyKey = builder.idempotencyKey;
    }

    public static CommandDispatchOptions defaults() {
        return DEFAULTS;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String correlationId() {
        return correlationId;
    }

    public String principal() {
        return principal;
    }

    public String idempotencyKey() {
        return idempotencyKey;
    }

    public static final class Builder {

        private String correlationId;
        private String principal;
        private String idempotencyKey;

        private Builder() {
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = normalize(correlationId);
            return this;
        }

        public Builder principal(String principal) {
            this.principal = normalize(principal);
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = normalize(idempotencyKey);
            return this;
        }

        public CommandDispatchOptions build() {
            return new CommandDispatchOptions(this);
        }

        private static String normalize(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }
            return Objects.requireNonNull(value).trim();
        }
    }
}
