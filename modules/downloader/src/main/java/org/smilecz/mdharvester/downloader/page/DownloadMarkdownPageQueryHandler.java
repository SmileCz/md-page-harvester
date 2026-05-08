package org.smilecz.mdharvester.downloader.page;

import jakarta.inject.Singleton;
import org.smilecz.mdharvester.cqrs.download.DownloadMarkdownPageQuery;
import org.smilecz.mdharvester.cqrs.download.DownloadedMarkdownPage;
import org.smilecz.mdharvester.cqrs.query.QueryHandler;
import java.util.Objects;

@Singleton
public final class DownloadMarkdownPageQueryHandler
        implements QueryHandler<DownloadMarkdownPageQuery, DownloadedMarkdownPage> {

    private final MarkdownPageDownloader downloader;

    public DownloadMarkdownPageQueryHandler(MarkdownPageDownloader downloader) {
        this.downloader = Objects.requireNonNull(downloader, "downloader");
    }

    @Override
    public Class<DownloadMarkdownPageQuery> queryType() {
        return DownloadMarkdownPageQuery.class;
    }

    @Override
    public DownloadedMarkdownPage handle(DownloadMarkdownPageQuery query) {
        return downloader.download(query);
    }
}
