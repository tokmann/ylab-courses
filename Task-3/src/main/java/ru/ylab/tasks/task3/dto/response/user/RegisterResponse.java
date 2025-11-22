package ru.ylab.tasks.task3.dto.response.user;

public class RegisterResponse {

    private String login;
    private String role;

    public RegisterResponse(String login, String role) {
        this.login = login;
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public String getRole() {
        return role;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
