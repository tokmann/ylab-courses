package ru.ylab.tasks.task1.repository;

import ru.ylab.tasks.task1.constant.Role;
import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.model.User;

import java.io.*;
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

    @Override
    public void save(User user) {
        users.put(user.getLogin(), user);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(users.get(login));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean existsByLogin(String login) {
        return users.containsKey(login);
    }

}
