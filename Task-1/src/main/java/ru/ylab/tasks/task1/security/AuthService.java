package ru.ylab.tasks.task1.security;

import ru.ylab.tasks.task1.constant.Role;
import ru.ylab.tasks.task1.exception.AccessDeniedException;
import ru.ylab.tasks.task1.model.User;
import ru.ylab.tasks.task1.repository.InMemoryUserRepository;
import ru.ylab.tasks.task1.repository.UserRepository;

import java.util.Optional;

/**
 * Сервис авторизации и регистрации пользователей.
 * Управляет текущей сессией пользователя и его ролью.
 */
public class AuthService {

    private final UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean login(String login, String password) {
        return userRepository.findByLogin(login)
                .filter(user -> user.getPassword().equals(password))
                .map(user -> {
                    currentUser = user;
                    return true;
                })
                .orElse(false);
    }

    public void logout() {
        currentUser = null;
    }


    /**
     * Регистрирует нового пользователя.
     * Если это первый пользователь — присваивается роль ADMIN.
     */
    public boolean register(String login, String password, String requestedRole) {
        if (login == null || login.isEmpty()) return false;
        if (userRepository.existsByLogin(login)) return false;

        Role role = determineAssignedRole(requestedRole);
        User u = new User(login, password, role);
        userRepository.save(u);
        return true;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public String getCurrentUserLogin() {
        return currentUser != null ? currentUser.getLogin() : null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Определяет, какую роль присвоить пользователю при регистрации.
     * Первый зарегистрированный пользователь — всегда ADMIN.
     */
    public Role determineAssignedRole(String requestedRole) {
        if (userRepository.findAll().isEmpty()) {
            return Role.ADMIN;
        }
        if (requestedRole == null || requestedRole.isEmpty()) {
            return Role.USER;
        }
        try {
            return Role.valueOf(requestedRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.USER;
        }
    }

    /**
     * Проверяет, что пользователь — администратор.
     * Если нет, выбрасывает исключение.
     */
    public void checkAdmin(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Нет прав для выполнения этой операции. Только ADMIN.");
        }
    }

}
