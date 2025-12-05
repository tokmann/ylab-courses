package ru.ylab.tasks.task4.dto.request.user;

/**
 * DTO для запроса на аутентификацию пользователя.
 * Содержит учетные данные для входа в систему.
 */
public class LoginRequest {

    private String login;
    private String password;

    public LoginRequest() {}

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
