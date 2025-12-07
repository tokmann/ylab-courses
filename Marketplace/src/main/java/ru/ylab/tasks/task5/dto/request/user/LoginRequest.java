package ru.ylab.tasks.task5.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на аутентификацию пользователя.
 * Содержит учетные данные для входа в систему.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    private String login;
    private String password;

}
