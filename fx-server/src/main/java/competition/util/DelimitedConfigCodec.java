package competition.util;

import java.util.List;

/**
 * Central codec for the legacy delimiter-based page and style configuration.
 * Keeping this class free of JavaFX dependencies also makes the storage format
 * testable in a headless build.
 */
public final class DelimitedConfigCodec {

    static final String FIELD_DELIMITER = "%%";
    static final String SECTION_DELIMITER = "###";

    private DelimitedConfigCodec() {
    }

    public static String[] splitFields(String value) {
        return value.split(FIELD_DELIMITER);
    }

    public static String[] splitSections(String value) {
        return value.split(SECTION_DELIMITER);
    }

    public static String joinFields(List<?> values) {
        return join(values, FIELD_DELIMITER);
    }

    public static String joinSections(List<?> values) {
        return join(values, SECTION_DELIMITER);
    }

    private static String join(List<?> values, String delimiter) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Object value : values) {
            if (!first) {
                result.append(delimiter);
            }
            result.append(value == null ? "" : value);
            first = false;
        }
        return result.toString();
    }
}
