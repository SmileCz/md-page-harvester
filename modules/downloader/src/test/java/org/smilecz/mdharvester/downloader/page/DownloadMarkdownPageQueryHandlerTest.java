package org.smilecz.mdharvester.downloader.page;

import static org.assertj.core.api.Assertions.assertThat;

import org.smilecz.mdharvester.cqrs.download.DownloadMarkdownPageQuery;
import org.smilecz.mdharvester.cqrs.download.DownloadedMarkdownPage;
import java.net.URI;
import org.junit.jupiter.api.Test;

final class DownloadMarkdownPageQueryHandlerTest {

    @Test
    void exposesDownloadQueryTypeAndDelegatesToDownloader() {
        DownloadedMarkdownPage expectedPage = new DownloadedMarkdownPage("page.md", "# Page");
        DownloadMarkdownPageQueryHandler handler =
                new DownloadMarkdownPageQueryHandler(query -> expectedPage);
        DownloadMarkdownPageQuery query =
                new DownloadMarkdownPageQuery(URI.create("https://example.com/page.md"), null, null);

        DownloadedMarkdownPage page = handler.handle(query);

        assertThat(handler.queryType()).isEqualTo(DownloadMarkdownPageQuery.class);
        assertThat(page).isEqualTo(expectedPage);
    }
}
