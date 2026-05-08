package org.smilecz.mdharvester.downloader.page;

import org.smilecz.mdharvester.cqrs.download.DownloadMarkdownPageQuery;
import org.smilecz.mdharvester.cqrs.download.DownloadedMarkdownPage;

public interface MarkdownPageDownloader {

    DownloadedMarkdownPage download(DownloadMarkdownPageQuery query);
}
