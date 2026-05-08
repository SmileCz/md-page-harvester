package org.smilecz.mdharvester.downloader.page;

import jakarta.inject.Singleton;
import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

@Singleton
public final class HttpMarkdownPageSourceClient implements MarkdownPageSourceClient {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);
    private static final String ACCEPT_MARKDOWN = "text/markdown, text/plain, */*";

    private final HttpClient httpClient;

    public HttpMarkdownPageSourceClient() {
        this(HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build());
    }

    HttpMarkdownPageSourceClient(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
    }

    @Override
    public String fetch(URI sourceUri) {
        HttpRequest request = HttpRequest.newBuilder(sourceUri)
                .timeout(REQUEST_TIMEOUT)
                .header("Accept", ACCEPT_MARKDOWN)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            return bodyOrThrow(response);
        } catch (IOException exception) {
            throw new DownloadMarkdownPageException("Markdown page download failed.", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new DownloadMarkdownPageException("Markdown page download was interrupted.", exception);
        }
    }

    private static String bodyOrThrow(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode > 299) {
            throw new DownloadMarkdownPageException(
                    "Markdown page download failed with HTTP status " + statusCode + "."
            );
        }
        return response.body();
    }
}
