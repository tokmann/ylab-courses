package ru.ylab.tasks.task4.security;

import ru.ylab.tasks.task4.constant.Role;
import ru.ylab.tasks.task4.exception.AccessDeniedException;
import ru.ylab.tasks.task4.model.User;
import ru.ylab.tasks.task4.repository.IUserRepository;

/**
 * Сервис авторизации и регистрации пользователей.
 * Управляет текущей сессией пользователя и его ролью.
 */
public class AuthServiceImpl implements IAuthService {

    private final IUserRepository userRepository;
    private User currentUser;

    public AuthServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Выполняет вход пользователя по логину и паролю.
     * Если логин и пароль верны, текущая сессия обновляется.
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return true, если вход успешен, иначе false
     */
    @Override
    public boolean login(String login, String password) {
        return userRepository.findByLogin(login)
                .filter(user -> user.getPassword().equals(password))
                .map(user -> {
                    currentUser = user;
                    return true;
                })
                .orElse(false);
    }

    /**
     * Выполняет выход текущего пользователя.
     * Очищает текущую сессию.
     */
    @Override
    public void logout() {
        currentUser = null;
    }


    /**
     * Регистрирует нового пользователя.
     * Если это первый пользователь в системе — автоматически присваивается роль {@link Role#ADMIN}.
     * @param login         логин нового пользователя
     * @param password      пароль нового пользователя
     * @param requestedRole желаемая роль (ADMIN/USER). Если null или некорректно, присваивается USER
     * @return true, если регистрация прошла успешно, иначе false
     */
    @Override
    public boolean register(String login, String password, String requestedRole) {
        if (login == null || login.isEmpty()) return false;
        if (userRepository.existsByLogin(login)) return false;

        Role role = determineAssignedRole(requestedRole);
        User u = new User(login, password, role);
        userRepository.save(u);
        return true;
    }

    /**
     * Проверяет, есть ли текущий аутентифицированный пользователь.
     * @return true, если пользователь вошел в систему
     */
    @Override
    public boolean isAuthenticated() {
        return currentUser != null;
    }

    @Override
    public boolean userExists(String login) {
        try {
            return userRepository.findByLogin(login).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Возвращает логин текущего пользователя.
     * @return логин пользователя или null, если пользователь не аутентифицирован
     */
    @Override
    public String getCurrentUserLogin() {
        return currentUser != null ? currentUser.getLogin() : null;
    }

    /**
     * Возвращает объект текущего пользователя.
     * @return текущий пользователь или null, если пользователь не аутентифицирован
     */
    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Определяет, какую роль присвоить пользователю при регистрации.
     * Первый зарегистрированный пользователь — всегда ADMIN.
     */
    @Override
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
    @Override
    public void checkAdmin(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Нет прав для выполнения этой операции. Только ADMIN.");
        }
    }

}
