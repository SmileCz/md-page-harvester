package org.smilecz.mdharvester.downloader.page;

import static org.assertj.core.api.Assertions.assertThat;

import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageCommand;
import java.net.URI;
import org.junit.jupiter.api.Test;

final class SafeMarkdownFileNameResolverTest {

    private final MarkdownFileNameResolver resolver = new SafeMarkdownFileNameResolver();

    @Test
    void usesRequestedFileNameBeforeTitleAndUri() {
        DownloadMarkdownPageCommand command = new DownloadMarkdownPageCommand(
                URI.create("https://example.com/docs/page.md"),
                "Title",
                "docs/\u00davod do API.md"
        );

        String fileName = resolver.resolve(command);

        assertThat(fileName).isEqualTo("docs-uvod-do-api.md");
    }

    @Test
    void fallsBackToTitleWhenFileNameIsMissing() {
        DownloadMarkdownPageCommand command = new DownloadMarkdownPageCommand(
                URI.create("https://example.com/docs/page.md"),
                "Api Reference",
                null
        );

        String fileName = resolver.resolve(command);

        assertThat(fileName).isEqualTo("api-reference.md");
    }

    @Test
    void fallsBackToUriPathWhenFileNameAndTitleAreMissing() {
        DownloadMarkdownPageCommand command = new DownloadMarkdownPageCommand(
                URI.create("https://example.com/docs/api%20guide.md"),
                null,
                null
        );

        String fileName = resolver.resolve(command);

        assertThat(fileName).isEqualTo("api-guide.md");
    }

    @Test
    void fallsBackToDefaultNameWhenNoSourceNameIsAvailable() {
        DownloadMarkdownPageCommand command = new DownloadMarkdownPageCommand(
                URI.create("https://example.com"),
                null,
                null
        );

        String fileName = resolver.resolve(command);

        assertThat(fileName).isEqualTo("page.md");
    }
}
