package ru.ylab.tasks.task3.dto.response.user;

/**
 * DTO для ответа на успешный выход из системы.
 * Содержит сообщение о результате выхода.
 */
public class LogoutResponse {

    private String message;

    public LogoutResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
