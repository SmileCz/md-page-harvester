package org.smilecz.mdharvester.downloader.page;

import static org.assertj.core.api.Assertions.assertThat;

import org.smilecz.mdharvester.cqrs.download.DownloadMarkdownPageQuery;
import java.net.URI;
import org.junit.jupiter.api.Test;

final class SafeMarkdownFileNameResolverTest {

    private final MarkdownFileNameResolver resolver = new SafeMarkdownFileNameResolver();

    @Test
    void usesRequestedFileNameBeforeTitleAndUri() {
        DownloadMarkdownPageQuery query = new DownloadMarkdownPageQuery(
                URI.create("https://example.com/docs/page.md"),
                "Title",
                "docs/\u00davod do API.md"
        );

        String fileName = resolver.resolve(query);

        assertThat(fileName).isEqualTo("docs-uvod-do-api.md");
    }

    @Test
    void fallsBackToTitleWhenFileNameIsMissing() {
        DownloadMarkdownPageQuery query = new DownloadMarkdownPageQuery(
                URI.create("https://example.com/docs/page.md"),
                "Api Reference",
                null
        );

        String fileName = resolver.resolve(query);

        assertThat(fileName).isEqualTo("api-reference.md");
    }

    @Test
    void fallsBackToUriPathWhenFileNameAndTitleAreMissing() {
        DownloadMarkdownPageQuery query = new DownloadMarkdownPageQuery(
                URI.create("https://example.com/docs/api%20guide.md"),
                null,
                null
        );

        String fileName = resolver.resolve(query);

        assertThat(fileName).isEqualTo("api-guide.md");
    }

    @Test
    void fallsBackToDefaultNameWhenNoSourceNameIsAvailable() {
        DownloadMarkdownPageQuery query = new DownloadMarkdownPageQuery(
                URI.create("https://example.com"),
                null,
                null
        );

        String fileName = resolver.resolve(query);

        assertThat(fileName).isEqualTo("page.md");
    }
}
