package ru.ylab.tasks.task5.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для ответа на успешную регистрацию пользователя.
 * Содержит информацию о зарегистрированном пользователе.
 */
@Data
@AllArgsConstructor
public class RegisterResponse {

    private String login;
    private String role;

}
