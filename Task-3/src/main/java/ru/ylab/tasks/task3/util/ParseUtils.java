package ru.ylab.tasks.task3.util;

import java.math.BigDecimal;

public final class ParseUtils {

    private ParseUtils() {}

    public static BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
