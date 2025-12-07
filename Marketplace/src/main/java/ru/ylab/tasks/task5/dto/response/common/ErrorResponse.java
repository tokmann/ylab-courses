package ru.ylab.tasks.task5.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO для ответа с информацией об ошибке.
 * Содержит основное сообщение и детали ошибки.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private List<String> details;

}

