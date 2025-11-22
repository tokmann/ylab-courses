package ru.ylab.tasks.task3.util.validation;

import ru.ylab.tasks.task3.dto.request.user.LoginRequest;
import ru.ylab.tasks.task3.dto.request.user.RegisterRequest;

import java.util.ArrayList;
import java.util.List;

public final class UserValidator {

    private UserValidator() {}

    public static List<String> validateLogin(LoginRequest dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getLogin() == null || dto.getLogin().isBlank()) {
            errors.add("login must not be empty");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            errors.add("password must not be empty");
        }
        return errors;
    }

    public static List<String> validateRegister(RegisterRequest dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getLogin() == null || dto.getLogin().isBlank()) {
            errors.add("login must not be empty");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            errors.add("password must not be empty");
        }
        return errors;
    }


}
