package org.smilecz.mdharvester.commands.download;

public final class DownloadMarkdownPageException extends RuntimeException {

    public DownloadMarkdownPageException(String message) {
        super(message);
    }

    public DownloadMarkdownPageException(String message, Throwable cause) {
        super(message, cause);
    }
}
