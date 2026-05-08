package org.smilecz.mdharvester.downloader.page;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ObsidianPublishMarkdownSourceResolver {

    private static final Pattern PRELOAD_PAGE = Pattern.compile("window\\.preloadPage=f\\(\"([^\"]+\\.md)\"\\)");

    Optional<URI> resolve(URI originalUri, String responseBody) {
        if (!isObsidianPublishUri(originalUri)) {
            return Optional.empty();
        }

        Matcher matcher = PRELOAD_PAGE.matcher(responseBody);
        if (!matcher.find()) {
            return Optional.empty();
        }

        URI markdownUri = URI.create(matcher.group(1));
        if (!markdownUri.isAbsolute()) {
            return Optional.empty();
        }
        return Optional.of(markdownUri);
    }

    private static boolean isObsidianPublishUri(URI uri) {
        return "publish.obsidian.md".equalsIgnoreCase(uri.getHost());
    }
}
