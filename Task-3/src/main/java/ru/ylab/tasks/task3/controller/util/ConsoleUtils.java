package ru.ylab.tasks.task3.controller.util;

import java.math.BigDecimal;
import java.util.Scanner;


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
     * Считывает и проверяет значение id из консоли.
     * Если пользователь вводит некорректный id, метод выводит сообщение с примером и повторяет ввод.
     * @param sc     объект для чтения ввода
     * @param prompt сообщение-приглашение для пользователя
     * @return корректный объект id
     */
    public static Long readLongId(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.err.println("Ошибка: введите корректный числовой ID. Пример: 1, 2, 3...");
            }
        }
    }

}
