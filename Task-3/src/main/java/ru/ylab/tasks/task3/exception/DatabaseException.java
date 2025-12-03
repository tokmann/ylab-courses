package ru.ylab.tasks.task3.exception;

public class DatabaseException extends RuntimeException {

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
