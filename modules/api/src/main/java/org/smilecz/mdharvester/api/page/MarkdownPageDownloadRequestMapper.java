package org.smilecz.mdharvester.api.page;

import jakarta.inject.Singleton;
import org.smilecz.mdharvester.cqrs.download.DownloadMarkdownPageQuery;
import org.smilecz.mdharvester.cqrs.download.InvalidDownloadMarkdownPageQueryException;

@Singleton
public final class MarkdownPageDownloadRequestMapper {

    public DownloadMarkdownPageQuery toQuery(MarkdownPageDownloadRequest request) {
        if (request == null) {
            throw new InvalidDownloadMarkdownPageQueryException("Request body is required.");
        }
        return DownloadMarkdownPageQuery.from(request.uri(), request.title(), request.fileName());
    }
}
