package ru.ylab.tasks.task1.util;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleUtils {

    public static String readNonEmptyString(Scanner sc, String prompt) {
        String line;
        do {
            System.out.print(prompt);
            line = sc.nextLine();
            if (line.isEmpty()) System.out.println("Ошибка: ввод не может быть пустым.");
        } while (line.isEmpty());
        return line;
    }

    public static BigDecimal readBigDecimal(Scanner sc, String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            if (allowEmpty && input.isEmpty()) return null;
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число.");
            }
        }
    }

    public static UUID readUUID(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: введите корректный UUID. Пример: 550e8400-e29b-41d4-a716-446655440000");
            }
        }
    }

}
