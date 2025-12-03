package ru.ylab.tasks.task3.controller.interfaces;

import ru.ylab.tasks.task3.exception.AccessDeniedException;
import ru.ylab.tasks.task3.model.User;

/**
 * Интерфейс контроллера для управления пользователями.
 * Определяет операции аутентификации, авторизации и регистрации пользователей.
 */
public interface IUserController {

    /**
     * Выполняет вход пользователя в систему.
     * @param login логин пользователя
     * @param password пароль пользователя
     * @return true если вход успешен, false в противном случае
     */
    boolean login(String login, String password);

    /**
     * Выполняет выход текущего пользователя из системы.
     */
    void logout();

    /**
     * Регистрирует нового пользователя в системе.
     * @param login логин нового пользователя
     * @param password пароль нового пользователя
     * @param requestedRole запрашиваемая роль пользователя
     * @return true если регистрация успешна, false в противном случае
     */
    boolean register(String login, String password, String requestedRole);

    /**
     * Проверяет, аутентифицирован ли текущий пользователь.
     * @return true если пользователь аутентифицирован, false в противном случае
     */
    boolean isAuthenticated();

    /**
     * Возвращает текущего аутентифицированного пользователя.
     * @return объект текущего пользователя
     */
    User currentUser();

    /**
     * Проверяет, имеет ли пользователь права администратора.
     * @param user пользователь для проверки
     */
    void checkAdmin(User user) throws AccessDeniedException ;
}
