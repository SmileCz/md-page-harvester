package org.smilecz.mdharvester.api.page;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import jakarta.inject.Singleton;
import org.smilecz.mdharvester.cqrs.download.DownloadedMarkdownPage;

@Singleton
public final class MarkdownPageDownloadResponseFactory {

    private static final String NO_STORE = "no-store";

    public HttpResponse<String> attachment(DownloadedMarkdownPage file) {
        return HttpResponse.ok(file.content())
                .contentType(MediaType.of(MarkdownMediaTypes.TEXT_MARKDOWN))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(file.fileName()))
                .header(HttpHeaders.CACHE_CONTROL, NO_STORE);
    }

    private static String contentDisposition(String fileName) {
        return "attachment; filename=\"" + fileName + "\"";
    }
}
