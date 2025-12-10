package ru.ylab.tasks.task5.util;

import java.math.BigDecimal;

/**
 * Утилитарный класс для парсинга строковых значений в BigDecimal.
 * Предоставляет безопасный метод преобразования с обработкой ошибок.
 */
public final class ParseUtils {

    private ParseUtils() {}

    /**
     * Парсит строковое значение в BigDecimal.
     * Возвращает null если строка пустая, null или содержит некорректное числовое значение.
     * @param value строковое значение для парсинга
     * @return объект BigDecimal или null если парсинг не удался
     */
    public static BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
