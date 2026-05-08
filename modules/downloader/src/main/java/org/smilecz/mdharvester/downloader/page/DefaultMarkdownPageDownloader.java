package org.smilecz.mdharvester.downloader.page;

import jakarta.inject.Singleton;
import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageCommand;
import org.smilecz.mdharvester.commands.download.DownloadedMarkdownPage;
import java.util.Objects;

@Singleton
public final class DefaultMarkdownPageDownloader implements MarkdownPageDownloader {

    private final MarkdownPageSourceClient sourceClient;
    private final MarkdownFileNameResolver fileNameResolver;

    public DefaultMarkdownPageDownloader(
            MarkdownPageSourceClient sourceClient,
            MarkdownFileNameResolver fileNameResolver
    ) {
        this.sourceClient = Objects.requireNonNull(sourceClient, "sourceClient");
        this.fileNameResolver = Objects.requireNonNull(fileNameResolver, "fileNameResolver");
    }

    @Override
    public DownloadedMarkdownPage download(DownloadMarkdownPageCommand command) {
        Objects.requireNonNull(command, "command");
        String content = sourceClient.fetch(command.sourceUri());
        String fileName = fileNameResolver.resolve(command);
        return new DownloadedMarkdownPage(fileName, content);
    }
}
