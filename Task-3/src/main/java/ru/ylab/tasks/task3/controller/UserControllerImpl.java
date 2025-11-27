package ru.ylab.tasks.task3.controller;

import ru.ylab.tasks.task3.constant.Role;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.exception.AccessDeniedException;
import ru.ylab.tasks.task3.model.User;
import ru.ylab.tasks.task3.security.AuthServiceImpl;
import ru.ylab.tasks.task3.security.IAuthService;
import ru.ylab.tasks.task3.service.audit.AuditServiceImpl;

/**
 * Контроллер, управляющий авторизацией и регистрацией пользователей.
 * Вызывает методы {@link AuthServiceImpl} и записывает действия через {@link AuditServiceImpl}.
 */
public class UserControllerImpl implements IUserController {

    private final IAuthService auth;

    public UserControllerImpl(IAuthService auth) {
        this.auth = auth;
    }

    /**
     * Выполняет вход пользователя в систему.
     * Логирует успешный или неуспешный вход через {@link AuditServiceImpl}.
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return true, если вход успешен; false — иначе
     */
    @Override
    public boolean login(String login, String password) {
        return auth.login(login, password);
    }

    /**
     * Выход текущего пользователя.
     * Логирует событие выхода и очищает текущую сессию.
     */
    @Override
    public void logout() {
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
    @Override
    public boolean register(String login, String password, String requestedRole) {
        Role assignedRole = auth.determineAssignedRole(requestedRole);
        return auth.register(login, password, String.valueOf(assignedRole));
    }

    /**
     * Проверяет, авторизован ли текущий пользователь.
     * @return true, если пользователь вошел в систему; false — иначе
     */
    @Override
    public boolean isAuthenticated() {
        return auth.isAuthenticated();
    }

    /**
     * Возвращает текущего пользователя.
     * @return объект {@link User} текущего пользователя, либо null если не авторизован
     */
    @Override
    public User currentUser() {
        return auth.getCurrentUser();
    }

    @Override
    public void checkAdmin(User user) throws AccessDeniedException {
        auth.checkAdmin(user);
    }

}
