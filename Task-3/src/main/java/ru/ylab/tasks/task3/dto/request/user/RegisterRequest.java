package ru.ylab.tasks.task3.dto.request.user;

public class RegisterRequest {

    private final String login;
    private final String password;
    private final String role;

    public RegisterRequest(String login, String password, String role) {
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

    public String getRole() {
        return role;
    }
}
