package ru.ylab.tasks.task5.repository;

import ru.ylab.tasks.task5.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления пользователями.
 * Определяет операции для поиска, сохранения и проверки существования пользователей.
 */
public interface UserRepository {
    /**
     * Сохраняет нового пользователя или обновляет существующего.
     * @param user пользователь для сохранения
     */
    void save(User user);

    /**
     * Ищет пользователя по id.
     * @param id пользователя
     * @return найденный пользователь (если существует)
     */
    Optional<User> findById(Long id);

    /**
     * Ищет пользователя по логину.
     * @param login логин пользователя
     * @return найденный пользователь (если существует)
     */
    Optional<User> findByLogin(String login);

    /**
     * Возвращает список всех пользователей.
     * @return список пользователей
     */
    List<User> findAll();

    /**
     * Проверяет, существует ли пользователь с указанным логином.
     * @param login логин для проверки
     * @return {@code true}, если пользователь существует
     */
    boolean existsByLogin(String login);

    /**
     * Удаляет пользователя по id
     * @param id пользователя
     * @return {@code true}, если удаление прошло успешно
     * */
    boolean deleteById(Long id);
}
