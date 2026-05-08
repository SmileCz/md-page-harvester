package org.smilecz.mdharvester.downloader.page;

import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageCommand;
import org.smilecz.mdharvester.commands.download.DownloadedMarkdownPage;

public interface MarkdownPageDownloader {

    DownloadedMarkdownPage download(DownloadMarkdownPageCommand command);
}
