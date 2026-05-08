package org.smilecz.mdharvester.commands.download;

import org.smilecz.mdharvester.commands.Command;
import java.net.URI;
import java.util.Locale;

public record DownloadMarkdownPageCommand(URI sourceUri, String title, String requestedFileName)
        implements Command<DownloadedMarkdownPage> {

    public DownloadMarkdownPageCommand {
        if (sourceUri == null) {
            throw new InvalidDownloadMarkdownPageCommandException("Source URI is required.");
        }
        if (!sourceUri.isAbsolute()) {
            throw new InvalidDownloadMarkdownPageCommandException("Source URI must be absolute.");
        }

        String scheme = sourceUri.getScheme().toLowerCase(Locale.ROOT);
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            throw new InvalidDownloadMarkdownPageCommandException("Source URI must use http or https.");
        }

        title = TextValues.trimToNull(title);
        requestedFileName = TextValues.trimToNull(requestedFileName);
    }

    public static DownloadMarkdownPageCommand from(String sourceUri, String title, String requestedFileName) {
        if (TextValues.isBlank(sourceUri)) {
            throw new InvalidDownloadMarkdownPageCommandException("Source URI is required.");
        }

        try {
            return new DownloadMarkdownPageCommand(URI.create(sourceUri.trim()), title, requestedFileName);
        } catch (IllegalArgumentException exception) {
            throw new InvalidDownloadMarkdownPageCommandException("Source URI is not valid.", exception);
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
