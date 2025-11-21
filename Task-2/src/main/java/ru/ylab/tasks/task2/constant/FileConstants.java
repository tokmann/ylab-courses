package ru.ylab.tasks.task2.constant;

/**
 * Константы для работы с файловой системой.
 * Включают имена файлов, разделители и регулярные выражения для парсинга данных.
 */
public final class FileConstants {

    private FileConstants() {}

    /** Имя файла с продуктами */
    public static final String PRODUCT_FILE = "product.txt";

    /** Имя файла с пользователями */
    public static final String USER_FILE = "uesrs.txt";

    /** Разделитель полей в файле */
    public static final String DELIMITER = "|";

    /** Рег. выражение для разбора строк из файла */
    public static String SPLIT_REGEX = "\\|";
}
