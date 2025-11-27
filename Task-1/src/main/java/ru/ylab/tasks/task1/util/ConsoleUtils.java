package ru.ylab.tasks.task1.util;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.UUID;


/**
 * Утилитный класс для безопасного чтения данных из консоли.
 * Содержит методы для ввода строк, чисел и UUID с валидацией и подсказками.
 */
public final class ConsoleUtils {

    private ConsoleUtils() {}

    /**
     * Считывает непустую строку из консоли.
     Если пользователь вводит пустую строку, метод выводит сообщение об ошибке и повторяет запрос.
     * @param sc     используемый для чтения ввода
     * @param prompt сообщение-приглашение для пользователя
     * @return введённая непустая строка
     */
    public static String readNonEmptyString(Scanner sc, String prompt) {
        String line;
        do {
            System.out.print(prompt);
            line = sc.nextLine();
            if (line.isEmpty()) System.out.println("Ошибка: ввод не может быть пустым.");
        } while (line.isEmpty());
        return line;
    }

    /**
     * Считывает значение типа BigDecimal из консоли.
     * @param sc          объект для чтения ввода
     * @param prompt      сообщение-приглашение для пользователя
     * @param allowEmpty  если true, разрешает пустой ввод
     * @return корректное значение BigDecimal или null, если пустой ввод разрешён
     */
    public static BigDecimal readBigDecimal(Scanner sc, String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            if (allowEmpty && input.isEmpty()) return null;
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.err.println("Ошибка: введите корректное число.");
            }
        }
    }

    /**
     * Считывает и проверяет значение UUID из консоли.
     * Если пользователь вводит некорректный UUID, метод выводит сообщение с примером и повторяет ввод.
     * @param sc     объект для чтения ввода
     * @param prompt сообщение-приглашение для пользователя
     * @return корректный объект UUID
     */
    public static UUID readUUID(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                System.err.println("Ошибка: введите корректный UUID. Пример: 550e8400-e29b-41d4-a716-446655440000");
            }
        }
    }

}
