package ru.ylab.tasks.task3.servlet.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.tasks.task3.constant.ResponseMessages;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.dto.request.user.LoginRequest;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.user.LoginResponse;
import ru.ylab.tasks.task3.util.validation.UserValidator;

import java.io.IOException;
import java.util.List;

import static ru.ylab.tasks.task3.constant.ResponseMessages.*;

/**
 * Сервлет для аутентификации пользователей в системе маркетплейса.
 * Обрабатывает POST запросы по пути "/marketplace/auth/login".
 * Проверяет учетные данные пользователя и устанавливает сессию при успешной аутентификации.
 */
@WebServlet("/marketplace/auth/login")
public class LoginServlet extends HttpServlet {

    private IUserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Инициализирует сервлет, получая контроллер пользователей из контекста приложения.
     */
    @Override
    public void init() {
        this.userController = (IUserController) getServletContext().getAttribute("userController");
    }

    /**
     * Обрабатывает POST запрос на аутентификацию пользователя.
     * Выполняет последовательно: парсинг DTO, валидацию данных, попытку входа
     * и отправку соответствующего ответа.
     * @param req HTTP запрос
     * @param resp HTTP ответ
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        LoginRequest dto = parseLoginRequest(req, resp);
        if (dto == null) {
            return;
        }

        if (!validateLoginData(dto, resp)) {
            return;
        }

        boolean success = attemptLogin(dto, resp);
        if (!success) {
            return;
        }

        sendSuccessResponse(resp);
    }

    /**
     * Парсит DTO запроса на аутентификацию из тела HTTP запроса.
     * @param req HTTP запрос
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return DTO запроса или null в случае ошибки парсинга
     */
    private LoginRequest parseLoginRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            return objectMapper.readValue(req.getInputStream(), LoginRequest.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(INVALID_JSON, List.of(e.getMessage())));
            return null;
        }
    }

    /**
     * Валидирует данные для входа пользователя.
     * @param dto DTO запроса на аутентификацию
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если данные валидны, false в противном случае
     */
    private boolean validateLoginData(LoginRequest dto, HttpServletResponse resp) throws IOException {
        List<String> errors = UserValidator.validateLogin(dto);
        if (!errors.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.VALIDATION_FAILED, errors));
            return false;
        }
        return true;
    }

    /**
     * Выполняет попытку аутентификации пользователя с предоставленными учетными данными.
     * @param dto DTO запроса на аутентификацию
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если аутентификация успешна, false в противном случае
     */
    private boolean attemptLogin(LoginRequest dto, HttpServletResponse resp) throws IOException {
        boolean success = userController.login(dto.getLogin(), dto.getPassword());

        if (!success) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(VALIDATION_FAILED, List.of("Bad login or password")));
            return false;
        }

        return true;
    }

    /**
     * Отправляет успешный ответ об аутентификации.
     * @param resp HTTP ответ
     */
    private void sendSuccessResponse(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), new LoginResponse(USER_LOGIN_SUCCESS));
    }
}
