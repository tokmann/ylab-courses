package ru.ylab.tasks.task3.repository.jdbc;

import ru.ylab.tasks.task3.constant.Role;
import ru.ylab.tasks.task3.db.ConnectionFactory;
import ru.ylab.tasks.task3.model.User;
import ru.ylab.tasks.task3.repository.IUserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.ylab.tasks.task3.constant.SqlConstants.*;

/**
 * Реализация репозитория пользователей на основе JDBC.
 * Обеспечивает операции CRUD для пользователей системы.
 */
public class JdbcUserRepositoryImpl implements IUserRepository {

    public JdbcUserRepositoryImpl() {}

    /**
     * Сохраняет пользователя в базе данных.
     * Если пользователь новый (id = null), выполняет вставку, иначе - обновление.
     * @param user пользователь для сохранения
     */
    @Override
    public void save(User user) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            if (user.getId() == null) {
                try (PreparedStatement ps = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, user.getLogin());
                    ps.setString(2, user.getPassword());
                    ps.setString(3, user.getRole().name());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            user.setId(rs.getLong(1));
                        }
                    }
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(UPDATE_USER)) {
                    ps.setString(1, user.getLogin());
                    ps.setString(2, user.getPassword());
                    ps.setString(3, user.getRole().name());
                    ps.setLong(4, user.getId());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения пользователя: " + e.getMessage(), e);
        }
    }

    /**
     * Находит пользователя по идентификатору.
     * @param id идентификатор пользователя
     * @return Optional с пользователем, если найден, иначе пустой Optional
     */
    @Override
    public Optional<User> findById(Long id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска пользователя по id: " + e.getMessage(), e);
        }
    }

    /**
     * Находит пользователя по логину.
     * @param login логин пользователя
     * @return Optional с пользователем, если найден, иначе пустой Optional
     */
    @Override
    public Optional<User> findByLogin(String login) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_LOGIN)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска пользователя по login: " + e.getMessage(), e);
        }
    }

    /**
     * Возвращает всех пользователей из базы данных.
     * @return список всех пользователей
     */
    @Override
    public List<User> findAll() {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL_USERS)) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения всех пользователей: " + e.getMessage(), e);
        }
    }

    /**
     * Проверяет существование пользователя с указанным логином.
     * @param login логин для проверки
     * @return true если пользователь существует, false в противном случае
     */
    @Override
    public boolean existsByLogin(String login) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_USER_BY_LOGIN)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка проверки существования login: " + e.getMessage(), e);
        }
    }

    /**
     * Удаляет пользователя по идентификатору.
     * @param id идентификатор пользователя для удаления
     * @return true если пользователь был удален, false если пользователь не найден
     */
    @Override
    public boolean deleteById(Long id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_USER_BY_ID)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления пользователя: " + e.getMessage(), e);
        }
    }

    /**
     * Преобразует ResultSet в объект User.
     * @param rs ResultSet с данными пользователя
     * @return объект User
     */
    private User mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String login = rs.getString("login");
        String password = rs.getString("password");
        String roleStr = rs.getString("role");
        Role role = roleStr != null ? Role.valueOf(roleStr) : null;
        return new User(id, login, password, role);
    }

}
