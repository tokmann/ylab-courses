package ru.ylab.tasks.task2.security;

import ru.ylab.tasks.task2.constant.Role;
import ru.ylab.tasks.task2.model.User;

/**
 * Интерфейс сервиса аутентификации и регистрации пользователей.
 */
public interface IAuthService {

    /**
     * Выполняет вход пользователя по логину и паролю.
     * @return true — если вход успешен
     */
    boolean login(String login, String password);

    /**
     * Выполняет выход пользователя.
     */
    void logout();

    /**
     * Регистрирует нового пользователя.
     */
    boolean register(String login, String password, String requestedRole);

    /**
     * Проверяет, что пользователь вошёл в систему.
     */
    boolean isAuthenticated();

    /**
     * Возвращает логин текущего пользователя (или null, если не аутентифицирован).
     */
    String getCurrentUserLogin();

    /**
     * Возвращает текущего пользователя.
     */
    User getCurrentUser();

    /**
     * Проверяет, что пользователь имеет роль ADMIN.
     */
    void checkAdmin(User user);

    /**
     * Определяет, какую роль присвоить при регистрации.
     */
    Role determineAssignedRole(String requestedRole);
}
