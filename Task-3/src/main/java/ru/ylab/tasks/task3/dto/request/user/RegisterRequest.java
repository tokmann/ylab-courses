package ru.ylab.tasks.task3.dto.request.user;

public class RegisterRequest {

    private String login;
    private String password;
    private String role;

    public RegisterRequest() {}

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
