package org.smilecz.mdharvester.cqrs.download;

import org.smilecz.mdharvester.cqrs.query.Query;
import java.net.URI;
import java.util.Locale;

public record DownloadMarkdownPageQuery(URI sourceUri, String title, String requestedFileName)
        implements Query<DownloadedMarkdownPage> {

    public DownloadMarkdownPageQuery {
        if (sourceUri == null) {
            throw new InvalidDownloadMarkdownPageQueryException("Source URI is required.");
        }
        if (!sourceUri.isAbsolute()) {
            throw new InvalidDownloadMarkdownPageQueryException("Source URI must be absolute.");
        }

        String scheme = sourceUri.getScheme().toLowerCase(Locale.ROOT);
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            throw new InvalidDownloadMarkdownPageQueryException("Source URI must use http or https.");
        }

        title = TextValues.trimToNull(title);
        requestedFileName = TextValues.trimToNull(requestedFileName);
    }

    public static DownloadMarkdownPageQuery from(String sourceUri, String title, String requestedFileName) {
        if (TextValues.isBlank(sourceUri)) {
            throw new InvalidDownloadMarkdownPageQueryException("Source URI is required.");
        }

        try {
            return new DownloadMarkdownPageQuery(URI.create(sourceUri.trim()), title, requestedFileName);
        } catch (IllegalArgumentException exception) {
            throw new InvalidDownloadMarkdownPageQueryException("Source URI is not valid.", exception);
        }
    }

    public String fileNameSource() {
        if (requestedFileName != null) {
            return requestedFileName;
        }
        if (title != null) {
            return title;
        }
        return lastPathSegment();
    }

    private String lastPathSegment() {
        String path = sourceUri.getPath();
        if (TextValues.isBlank(path) || "/".equals(path)) {
            return null;
        }

        int lastSlash = path.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash == path.length() - 1) {
            return path;
        }
        return path.substring(lastSlash + 1);
    }
}
