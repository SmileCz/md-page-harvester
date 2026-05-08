package org.smilecz.mdharvester.api.page;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record ApiErrorResponse(String message) {
}
