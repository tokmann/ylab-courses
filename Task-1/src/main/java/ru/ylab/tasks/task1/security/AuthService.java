package ru.ylab.tasks.task1.security;

import ru.ylab.tasks.task1.constant.Role;
import ru.ylab.tasks.task1.model.User;
import ru.ylab.tasks.task1.repository.UserRepository;

import java.util.Optional;

/**
 * Сервис авторизации и регистрации пользователей.
 * Управляет текущей сессией пользователя и его ролью.
 */
public class AuthService {

    private final UserRepository userRepository = new UserRepository();
    private User currentUser;

    public boolean login(String login, String password) {
        Optional<User> user = userRepository.findByLogin(login);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            currentUser = user.get();
            return true;
        }
        return false;
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
        if (userRepository.getAll().isEmpty()) {
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

    public void saveToFile() {
        userRepository.saveToFile();
    }

    public void loadFromFile() {
        userRepository.loadFromFile();
    }
}
