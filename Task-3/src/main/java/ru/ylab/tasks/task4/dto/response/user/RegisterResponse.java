package ru.ylab.tasks.task4.dto.response.user;

/**
 * DTO для ответа на успешную регистрацию пользователя.
 * Содержит информацию о зарегистрированном пользователе.
 */
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
