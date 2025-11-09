package ru.ylab.tasks.task1.model;

import ru.ylab.tasks.task1.constant.Role;

/**
 * Модель пользователя системы.
 * Содержит логин, пароль и роль (USER или ADMIN).
 */
public class User {

    private final String login;
    private final String password;
    private final Role role;

    public User(String login, String password, Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User{" + "login='" + login + '\'' + ", role='" + role + '\'' + '}';
    }
}
