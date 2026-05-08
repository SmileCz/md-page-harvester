package org.smilecz.mdharvester.downloader.page;

import static org.assertj.core.api.Assertions.assertThat;

import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageCommand;
import org.smilecz.mdharvester.commands.download.DownloadedMarkdownPage;
import java.net.URI;
import org.junit.jupiter.api.Test;

final class DownloadMarkdownPageCommandHandlerTest {

    @Test
    void exposesDownloadCommandTypeAndDelegatesToDownloader() {
        DownloadedMarkdownPage expectedPage = new DownloadedMarkdownPage("page.md", "# Page");
        DownloadMarkdownPageCommandHandler handler =
                new DownloadMarkdownPageCommandHandler(command -> expectedPage);
        DownloadMarkdownPageCommand command =
                new DownloadMarkdownPageCommand(URI.create("https://example.com/page.md"), null, null);

        DownloadedMarkdownPage page = handler.handle(command);

        assertThat(handler.commandType()).isEqualTo(DownloadMarkdownPageCommand.class);
        assertThat(page).isEqualTo(expectedPage);
    }
}
