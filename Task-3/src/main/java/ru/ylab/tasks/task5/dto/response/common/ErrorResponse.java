package ru.ylab.tasks.task4.dto.response.common;

import java.util.List;

/**
 * DTO для ответа с информацией об ошибке.
 * Содержит основное сообщение и детали ошибки.
 */
public class ErrorResponse {

    private String message;
    private List<String> details;

    public ErrorResponse(String message, List<String> details) {
        this.message = message;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}

