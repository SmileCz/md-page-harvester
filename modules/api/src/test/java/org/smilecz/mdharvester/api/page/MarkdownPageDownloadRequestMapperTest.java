package org.smilecz.mdharvester.api.page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageCommand;
import org.smilecz.mdharvester.commands.download.InvalidDownloadMarkdownPageCommandException;
import org.junit.jupiter.api.Test;

final class MarkdownPageDownloadRequestMapperTest {

    private final MarkdownPageDownloadRequestMapper mapper = new MarkdownPageDownloadRequestMapper();

    @Test
    void mapsRequestToNormalizedCommand() {
        MarkdownPageDownloadRequest request =
                new MarkdownPageDownloadRequest(" https://example.com/docs/page.md ", "  Title  ", "  page.md  ");

        DownloadMarkdownPageCommand command = mapper.toCommand(request);

        assertThat(command.sourceUri().toString()).isEqualTo("https://example.com/docs/page.md");
        assertThat(command.title()).isEqualTo("Title");
        assertThat(command.requestedFileName()).isEqualTo("page.md");
    }

    @Test
    void rejectsMissingRequestBody() {
        assertThatThrownBy(() -> mapper.toCommand(null))
                .isInstanceOf(InvalidDownloadMarkdownPageCommandException.class)
                .hasMessage("Request body is required.");
    }

    @Test
    void rejectsBlankSourceUri() {
        MarkdownPageDownloadRequest request = new MarkdownPageDownloadRequest("   ", "Title", null);

        assertThatThrownBy(() -> mapper.toCommand(request))
                .isInstanceOf(InvalidDownloadMarkdownPageCommandException.class)
                .hasMessage("Source URI is required.");
    }
}
