package ru.ylab.tasks.task3.model;

import ru.ylab.tasks.task3.constant.Role;

/**
 * Модель пользователя системы.
 * Содержит логин, пароль и роль (USER или ADMIN).
 */
public class User {

    private Long id;
    private String login;
    private String password;
    private Role role;

    public User() {}

    public User(Long id, String login, String password, Role role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public User(String login, String password, Role role) {
        this(null, login, password, role);
    }

    public Long getId() {
        return id;
    }

    /**
     * Вызывается только репозиторием.
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {this.login = login; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" + "login='" + login + '\'' + ", role='" + role + '\'' + '}';
    }
}
