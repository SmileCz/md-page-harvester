package org.smilecz.mdharvester.commands.download;

final class TextValues {

    private TextValues() {
    }

    static boolean isBlank(String value) {
        return value == null || value.isBlank();
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
