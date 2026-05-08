package org.smilecz.mdharvester.api.page;

import jakarta.inject.Singleton;
import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageCommand;
import org.smilecz.mdharvester.commands.download.InvalidDownloadMarkdownPageCommandException;

@Singleton
public final class MarkdownPageDownloadRequestMapper {

    public DownloadMarkdownPageCommand toCommand(MarkdownPageDownloadRequest request) {
        if (request == null) {
            throw new InvalidDownloadMarkdownPageCommandException("Request body is required.");
        }
        return DownloadMarkdownPageCommand.from(request.uri(), request.title(), request.fileName());
    }
}
