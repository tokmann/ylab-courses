package ru.ylab.tasks.task1.controller;

import ru.ylab.tasks.task1.constant.AuditMessages;
import ru.ylab.tasks.task1.constant.Role;
import ru.ylab.tasks.task1.model.User;
import ru.ylab.tasks.task1.security.AuthService;
import ru.ylab.tasks.task1.service.AuditService;

import static ru.ylab.tasks.task1.constant.AuditMessages.*;

/**
 * Контроллер, управляющий авторизацией и регистрацией пользователей.
 * Вызывает методы {@link AuthService} и записывает действия через {@link AuditService}.
 */
public class UserController {

    private final AuthService auth;
    private final AuditService audit;

    public UserController(AuthService auth, AuditService audit) {
        this.auth = auth;
        this.audit = audit;
    }

    /**
     * Выполняет вход пользователя в систему.
     * Логирует успешный или неуспешный вход через {@link AuditService}.
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return true, если вход успешен; false — иначе
     */
    public boolean login(String login, String password) {
        boolean success = auth.login(login, password);
        if (success) {
            audit.log(String.format(LOGIN_SUCCESS, login));
        } else {
            audit.log(String.format(LOGIN_FAILED, login));
        }
        return success;
    }

    /**
     * Выход текущего пользователя.
     * Логирует событие выхода и очищает текущую сессию.
     */
    public void logout() {
        if (auth.isAuthenticated()) {
            audit.log(String.format(LOGOUT_SUCCESS, auth.getCurrentUserLogin()));
        }
        auth.logout();
    }

    /**
     * Регистрирует нового пользователя.
     * Если это первый пользователь — автоматически получает роль ADMIN.
     * Логирует успешную регистрацию или неудачную попытку.
     * @param login         логин нового пользователя
     * @param password      пароль нового пользователя
     * @param requestedRole запрашиваемая роль (ADMIN/USER), может быть null
     * @return true, если регистрация прошла успешно; false — иначе
     */
    public boolean register(String login, String password, String requestedRole) {
        Role assignedRole = auth.determineAssignedRole(requestedRole);
        boolean ok = auth.register(login, password, requestedRole);
        if (ok) {
            audit.log(String.format(AuditMessages.USER_REGISTERED, login, assignedRole));
        } else {
            audit.log(String.format(AuditMessages.USER_REGISTER_FAILED, login));
        }
        return ok;
    }

    /**
     * Проверяет, авторизован ли текущий пользователь.
     * @return true, если пользователь вошел в систему; false — иначе
     */
    public boolean isAuthenticated() {
        return auth.isAuthenticated();
    }

    /**
     * Возвращает текущего пользователя.
     * @return объект {@link User} текущего пользователя, либо null если не авторизован
     */
    public User currentUser() {
        return auth.getCurrentUser();
    }

}
