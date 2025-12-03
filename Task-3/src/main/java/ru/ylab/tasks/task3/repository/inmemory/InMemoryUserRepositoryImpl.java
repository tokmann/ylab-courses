package ru.ylab.tasks.task3.repository.inmemory;

import ru.ylab.tasks.task3.model.User;
import ru.ylab.tasks.task3.repository.IUserRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Репозиторий для хранения и управления пользователями.
 * Работает с внутренней мапой и сохраняет данные в файл users.txt.
 */
public class InMemoryUserRepositoryImpl implements IUserRepository {

    private final Map<Long, User> usersById = new HashMap<>();
    private final Map<String, User> usersByLogin = new HashMap<>();

    private final AtomicLong idGenerator = new AtomicLong(1);

    public InMemoryUserRepositoryImpl(Collection<User> initialUsers) {
        initialUsers.forEach(this::save);
    }

    /**
     * Сохраняет пользователя в репозитории.
     * @param user пользователь для сохранения
     */
    @Override
    public void save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        usersById.put(user.getId(), user);
        usersByLogin.put(user.getLogin(), user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(usersById.get(id));
    }

    /**
     * Находит пользователя по логину.
     * @param login логин пользователя
     * @return Optional с пользователем или пустой, если пользователь не найден
     */
    @Override
    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(usersByLogin.get(login));
    }

    /**
     * Возвращает список всех пользователей.
     * @return список всех пользователей
     */
    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    /**
     * Проверяет, существует ли пользователь с указанным логином.
     * @param login логин
     * @return true, если пользователь существует
     */
    @Override
    public boolean existsByLogin(String login) {
        return usersByLogin.containsKey(login);
    }

    @Override
    public boolean deleteById(Long id) {
        User removed = usersById.remove(id);
        if (removed != null) {
            usersByLogin.remove(removed.getLogin());
            return true;
        }
        return false;
    }

}

