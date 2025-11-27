package ru.ylab.tasks.task3.util.validation;

import ru.ylab.tasks.task3.dto.request.user.LoginRequest;
import ru.ylab.tasks.task3.dto.request.user.RegisterRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилитарный класс для валидации данных пользователей.
 * Предоставляет статические методы для проверки корректности данных
 * при аутентификации и регистрации пользователей.
 */
public final class UserValidator {

    private UserValidator() {}

    /**
     * Валидирует данные для входа пользователя в систему.
     * Проверяет наличие логина и пароля.
     * @param dto DTO запроса на аутентификацию
     * @return список ошибок валидации. Если список пуст, данные считаются валидными
     */
    public static List<String> validateLogin(LoginRequest dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getLogin() == null || dto.getLogin().isBlank()) {
            errors.add("Login must not be empty");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            errors.add("Password must not be empty");
        }
        return errors;
    }

    /**
     * Валидирует данные для регистрации нового пользователя.
     * Проверяет наличие логина и пароля.
     * @param dto DTO запроса на регистрацию
     * @return список ошибок валидации. Если список пуст, данные считаются валидными
     */
    public static List<String> validateRegister(RegisterRequest dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getLogin() == null || dto.getLogin().isBlank()) {
            errors.add("Login must not be empty");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            errors.add("Password must not be empty");
        }
        return errors;
    }


}
