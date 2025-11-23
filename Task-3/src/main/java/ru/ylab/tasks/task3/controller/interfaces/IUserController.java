package ru.ylab.tasks.task3.controller.interfaces;

import ru.ylab.tasks.task3.exception.AccessDeniedException;
import ru.ylab.tasks.task3.model.User;

public interface IUserController {

    boolean login(String login, String password);
    void logout();
    boolean register(String login, String password, String requestedRole);
    boolean isAuthenticated();
    User currentUser();
    void checkAdmin(User user) throws AccessDeniedException ;
}
