package org.smilecz.mdharvester.api.page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.smilecz.mdharvester.cqrs.download.DownloadMarkdownPageQuery;
import org.smilecz.mdharvester.cqrs.download.InvalidDownloadMarkdownPageQueryException;
import org.junit.jupiter.api.Test;

final class MarkdownPageDownloadRequestMapperTest {

    private final MarkdownPageDownloadRequestMapper mapper = new MarkdownPageDownloadRequestMapper();

    @Test
    void mapsRequestToNormalizedCommand() {
        MarkdownPageDownloadRequest request =
                new MarkdownPageDownloadRequest(" https://example.com/docs/page.md ", "  Title  ", "  page.md  ");

        DownloadMarkdownPageQuery query = mapper.toQuery(request);

        assertThat(query.sourceUri().toString()).isEqualTo("https://example.com/docs/page.md");
        assertThat(query.title()).isEqualTo("Title");
        assertThat(query.requestedFileName()).isEqualTo("page.md");
    }

    @Test
    void rejectsMissingRequestBody() {
        assertThatThrownBy(() -> mapper.toQuery(null))
                .isInstanceOf(InvalidDownloadMarkdownPageQueryException.class)
                .hasMessage("Request body is required.");
    }

    @Test
    void rejectsBlankSourceUri() {
        MarkdownPageDownloadRequest request = new MarkdownPageDownloadRequest("   ", "Title", null);

        assertThatThrownBy(() -> mapper.toQuery(request))
                .isInstanceOf(InvalidDownloadMarkdownPageQueryException.class)
                .hasMessage("Source URI is required.");
    }
}
