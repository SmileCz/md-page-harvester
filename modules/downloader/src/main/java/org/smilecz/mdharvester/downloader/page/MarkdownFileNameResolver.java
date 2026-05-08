package org.smilecz.mdharvester.downloader.page;

import org.smilecz.mdharvester.cqrs.download.DownloadMarkdownPageQuery;

public interface MarkdownFileNameResolver {

    String resolve(DownloadMarkdownPageQuery query);
}
