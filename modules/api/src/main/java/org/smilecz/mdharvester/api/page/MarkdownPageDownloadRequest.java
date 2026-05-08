package org.smilecz.mdharvester.api.page;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record MarkdownPageDownloadRequest(String uri, String title, String fileName) {
}
