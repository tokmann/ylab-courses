package ru.ylab.tasks.task1.repository;

import ru.ylab.tasks.task1.model.User;

import java.util.*;

/**
 * Репозиторий для хранения и управления пользователями.
 * Работает с внутренней мапой и сохраняет данные в файл users.txt.
 */
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    public InMemoryUserRepository(Collection<User> initialUsers) {
        initialUsers.forEach(this::save);
    }

    /**
     * Сохраняет пользователя в репозитории.
     * @param user пользователь для сохранения
     */
    @Override
    public void save(User user) {
        users.put(user.getLogin(), user);
    }

    /**
     * Находит пользователя по логину.
     * @param login логин пользователя
     * @return Optional с пользователем или пустой, если пользователь не найден
     */
    @Override
    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(users.get(login));
    }

    /**
     * Возвращает список всех пользователей.
     * @return список всех пользователей
     */
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Проверяет, существует ли пользователь с указанным логином.
     * @param login логин
     * @return true, если пользователь существует
     */
    @Override
    public boolean existsByLogin(String login) {
        return users.containsKey(login);
    }

}
