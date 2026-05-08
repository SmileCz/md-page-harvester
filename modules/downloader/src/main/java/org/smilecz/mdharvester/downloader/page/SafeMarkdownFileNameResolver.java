package org.smilecz.mdharvester.downloader.page;

import jakarta.inject.Singleton;
import org.smilecz.mdharvester.commands.download.DownloadMarkdownPageCommand;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Singleton
public final class SafeMarkdownFileNameResolver implements MarkdownFileNameResolver {

    private static final String DEFAULT_BASENAME = "page";
    private static final int MAX_BASENAME_LENGTH = 120;
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern UNSAFE_CHARACTERS = Pattern.compile("[^A-Za-z0-9._-]+");
    private static final Pattern REPEATED_DASH = Pattern.compile("-+");

    @Override
    public String resolve(DownloadMarkdownPageCommand command) {
        String source = TextValues.trimToNull(command.fileNameSource());
        String basename = sanitize(source == null ? DEFAULT_BASENAME : source);
        return basename + ".md";
    }

    private static String sanitize(String source) {
        String decoded = URLDecoder.decode(source, StandardCharsets.UTF_8);
        String ascii = Normalizer.normalize(decoded, Normalizer.Form.NFD);
        ascii = DIACRITICS.matcher(ascii).replaceAll("");
        ascii = withoutMarkdownExtension(ascii);

        String sanitized = UNSAFE_CHARACTERS.matcher(ascii).replaceAll("-");
        sanitized = REPEATED_DASH.matcher(sanitized).replaceAll("-");
        sanitized = trimDecorators(sanitized).toLowerCase(Locale.ROOT);

        if (sanitized.isBlank()) {
            return DEFAULT_BASENAME;
        }
        if (sanitized.length() > MAX_BASENAME_LENGTH) {
            sanitized = trimDecorators(sanitized.substring(0, MAX_BASENAME_LENGTH));
        }
        if (sanitized.isBlank()) {
            return DEFAULT_BASENAME;
        }
        return sanitized;
    }

    private static String withoutMarkdownExtension(String value) {
        if (value.toLowerCase(Locale.ROOT).endsWith(".md")) {
            return value.substring(0, value.length() - 3);
        }
        return value;
    }

    private static String trimDecorators(String value) {
        int start = 0;
        int end = value.length();

        while (start < end && isDecorator(value.charAt(start))) {
            start++;
        }
        while (end > start && isDecorator(value.charAt(end - 1))) {
            end--;
        }

        return value.substring(start, end);
    }

    private static boolean isDecorator(char character) {
        return character == '.' || character == '-' || character == '_';
    }
}
