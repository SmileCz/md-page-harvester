package org.smilecz.mdharvester.commands.download;

import java.util.Objects;

public record DownloadedMarkdownPage(String fileName, String content) {

    public DownloadedMarkdownPage {
        if (TextValues.isBlank(fileName)) {
            throw new IllegalArgumentException("fileName must not be blank");
        }
        content = Objects.requireNonNull(content, "content");
    }
}
