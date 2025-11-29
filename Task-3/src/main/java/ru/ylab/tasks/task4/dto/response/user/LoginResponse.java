package ru.ylab.tasks.task4.dto.response.user;

/**
 * DTO для ответа на успешную аутентификацию.
 * Содержит сообщение о результате входа.
 */
public class LoginResponse {

    private String message;

    public LoginResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
