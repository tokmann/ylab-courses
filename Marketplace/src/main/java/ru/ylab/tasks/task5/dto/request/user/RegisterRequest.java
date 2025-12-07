package ru.ylab.tasks.task5.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на регистрацию пользователя.
 * Содержит данные для создания нового пользователя.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String login;
    private String password;
    private String role;

}
