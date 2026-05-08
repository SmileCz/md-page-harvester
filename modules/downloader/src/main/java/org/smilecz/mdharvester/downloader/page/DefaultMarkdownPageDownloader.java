package org.smilecz.mdharvester.downloader.page;

import jakarta.inject.Singleton;
import org.smilecz.mdharvester.cqrs.download.DownloadMarkdownPageQuery;
import org.smilecz.mdharvester.cqrs.download.DownloadedMarkdownPage;
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
    public DownloadedMarkdownPage download(DownloadMarkdownPageQuery query) {
        Objects.requireNonNull(query, "query");
        String content = sourceClient.fetch(query.sourceUri());
        String fileName = fileNameResolver.resolve(query);
        return new DownloadedMarkdownPage(fileName, content);
    }
}
