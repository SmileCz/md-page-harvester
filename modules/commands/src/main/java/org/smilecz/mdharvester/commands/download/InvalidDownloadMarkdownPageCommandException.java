package org.smilecz.mdharvester.commands.download;

public final class InvalidDownloadMarkdownPageCommandException extends RuntimeException {

    public InvalidDownloadMarkdownPageCommandException(String message) {
        super(message);
    }

    public InvalidDownloadMarkdownPageCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
