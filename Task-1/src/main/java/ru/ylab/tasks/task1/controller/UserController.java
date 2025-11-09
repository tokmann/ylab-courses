package ru.ylab.tasks.task1.controller;

import ru.ylab.tasks.task1.constant.Role;
import ru.ylab.tasks.task1.model.User;
import ru.ylab.tasks.task1.security.AuthService;
import ru.ylab.tasks.task1.service.AuditService;

public class UserController {

    private final AuthService auth;
    private final AuditService audit;

    public UserController(AuthService auth, AuditService audit) {
        this.auth = auth;
        this.audit = audit;
    }

    public boolean login(String login, String password) {
        boolean success = auth.login(login, password);
        if (success) {
            audit.log("Пользователь вошел: " + login);
        } else {
            audit.log("Неудачная попытка входа: " + login);
        }
        return success;
    }

    public void logout() {
        if (auth.isAuthenticated()) {
            audit.log("Пользователь вышел: " + auth.getCurrentUserLogin());
        }
        auth.logout();
    }

    public boolean register(String login, String password, String requestedRole) {
        Role assignedRole = auth.determineAssignedRole(requestedRole);
        boolean ok = auth.register(login, password, requestedRole);
        if (ok) {
            audit.log("Новый пользователь зарегистрирован: " + login + " (role=" + assignedRole + ")");
        } else {
            audit.log("Неудачная попытка регистрации: " + login);
        }
        return ok;
    }

    public boolean isAuthenticated() {
        return auth.isAuthenticated();
    }

    public User currentUser() {
        return auth.getCurrentUser();
    }

}
