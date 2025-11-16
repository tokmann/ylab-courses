package ru.ylab.tasks.task1.repository.jdbc;

import ru.ylab.tasks.task1.constant.Role;
import ru.ylab.tasks.task1.model.User;
import ru.ylab.tasks.task1.repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {

    private final Connection connection;

    public JdbcUserRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(User user) {
        try {
            if (user.getId() == null) {
                String sql = "INSERT INTO marketplace.users (login, password, role) " +
                        "VALUES (?, ?, ?) RETURNING id";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, user.getLogin());
                    ps.setString(2, user.getPassword());
                    ps.setString(3, user.getRole().name());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            user.setId(rs.getLong("id"));
                        }
                    }
                }
            } else {
                String sql = "UPDATE marketplace.users SET login = ?, password = ?, role = ? WHERE id = ?";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
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

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, login, password, role FROM marketplace.users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска пользователя по id: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT id, login, password, role FROM marketplace.users WHERE login = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска пользователя по login: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, login, password, role FROM marketplace.users ORDER BY id";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения всех пользователей: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByLogin(String login) {
        String sql = "SELECT 1 FROM marketplace.users WHERE login = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка проверки существования login: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM marketplace.users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления пользователя: " + e.getMessage(), e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String login = rs.getString("login");
        String password = rs.getString("password");
        String roleStr = rs.getString("role");
        Role role = roleStr != null ? Role.valueOf(roleStr) : null;
        return new User(id, login, password, role);
    }
}
