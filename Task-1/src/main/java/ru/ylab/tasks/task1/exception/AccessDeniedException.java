package ru.ylab.tasks.task1.exception;

/**
 * Исключение, которое выбрасывается при попытке выполнения операции,
 * недоступной для текущего пользователя (если пользователь не является ADMIN).
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * Создает исключение с заданным сообщением.
     * @param message описание причины отказа в доступе
     */
    public AccessDeniedException(String message) {
        super(message);
    }

}
