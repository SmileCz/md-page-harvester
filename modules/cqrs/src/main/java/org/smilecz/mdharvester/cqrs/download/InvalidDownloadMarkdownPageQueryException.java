package org.smilecz.mdharvester.cqrs.download;

public final class InvalidDownloadMarkdownPageQueryException extends RuntimeException {

    public InvalidDownloadMarkdownPageQueryException(String message) {
        super(message);
    }

    public InvalidDownloadMarkdownPageQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
