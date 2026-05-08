package org.smilecz.mdharvester.downloader.page;

import java.net.URI;

public interface MarkdownPageSourceClient {

    String fetch(URI sourceUri);
}
