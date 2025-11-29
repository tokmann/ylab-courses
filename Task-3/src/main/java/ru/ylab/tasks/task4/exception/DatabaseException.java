package ru.ylab.tasks.task4.exception;

/**
 * Исключение, которое выбрасывается при ошибке работы с JDBC репозиториями.
 */
public class DatabaseException extends RuntimeException {

    /**
     * Создает исключение с заданным сообщением.
     * @param message сообщение ошибки
     * @param cause причина ошибки
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
