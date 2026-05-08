package org.smilecz.mdharvester.downloader.page;

import static org.assertj.core.api.Assertions.assertThat;

import org.smilecz.mdharvester.cqrs.download.DownloadMarkdownPageQuery;
import org.smilecz.mdharvester.cqrs.download.DownloadedMarkdownPage;
import java.net.URI;
import org.junit.jupiter.api.Test;

final class DefaultMarkdownPageDownloaderTest {

    private final CapturingSourceClient sourceClient = new CapturingSourceClient("# Api Guide");
    private final MarkdownPageDownloader downloader = new DefaultMarkdownPageDownloader(
            sourceClient,
            new SafeMarkdownFileNameResolver()
    );

    @Test
    void downloadsSourceContentAndReturnsNamedMarkdownPage() {
        DownloadMarkdownPageQuery query = new DownloadMarkdownPageQuery(
                URI.create("https://example.com/docs/api-guide.md"),
                null,
                null
        );

        DownloadedMarkdownPage page = downloader.download(query);

        assertThat(sourceClient.sourceUri).isEqualTo(URI.create("https://example.com/docs/api-guide.md"));
        assertThat(page.fileName()).isEqualTo("api-guide.md");
        assertThat(page.content()).isEqualTo("# Api Guide");
    }

    private static final class CapturingSourceClient implements MarkdownPageSourceClient {

        private final String content;
        private URI sourceUri;

        private CapturingSourceClient(String content) {
            this.content = content;
        }

        @Override
        public String fetch(URI sourceUri) {
            this.sourceUri = sourceUri;
            return content;
        }
    }
}
