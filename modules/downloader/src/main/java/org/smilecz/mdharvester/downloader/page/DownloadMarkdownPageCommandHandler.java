package org.smilecz.mdharvester.downloader.page;

import jakarta.inject.Singleton;
import org.smilecz.mdharvester.commands.CommandHandler;
import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageCommand;
import org.smilecz.mdharvester.commands.download.DownloadedMarkdownPage;
import java.util.Objects;

@Singleton
public final class DownloadMarkdownPageCommandHandler
        implements CommandHandler<DownloadMarkdownPageCommand, DownloadedMarkdownPage> {

    private final MarkdownPageDownloader downloader;

    public DownloadMarkdownPageCommandHandler(MarkdownPageDownloader downloader) {
        this.downloader = Objects.requireNonNull(downloader, "downloader");
    }

    @Override
    public Class<DownloadMarkdownPageCommand> commandType() {
        return DownloadMarkdownPageCommand.class;
    }

    @Override
    public DownloadedMarkdownPage handle(DownloadMarkdownPageCommand command) {
        return downloader.download(command);
    }
}
