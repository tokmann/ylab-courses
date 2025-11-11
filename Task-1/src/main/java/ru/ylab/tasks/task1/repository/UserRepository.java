package ru.ylab.tasks.task1.repository;

import ru.ylab.tasks.task1.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findByLogin(String login);
    List<User> findAll();
    boolean existsByLogin(String login);
}
