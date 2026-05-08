package org.smilecz.mdharvester.downloader.page;

import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageCommand;

public interface MarkdownFileNameResolver {

    String resolve(DownloadMarkdownPageCommand command);
}
