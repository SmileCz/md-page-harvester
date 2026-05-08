package org.smilecz.mdharvester.downloader.page;

final class TextValues {

    private TextValues() {
    }

    static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }
}
