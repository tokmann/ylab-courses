package ru.ylab.tasks.task5.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для ответа на успешную аутентификацию.
 * Содержит сообщение о результате входа.
 */
@Data
@AllArgsConstructor
public class LoginResponse {

    private String message;

}
