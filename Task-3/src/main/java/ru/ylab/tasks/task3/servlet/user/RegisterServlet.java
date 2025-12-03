package ru.ylab.tasks.task3.servlet.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import ru.ylab.tasks.task3.constant.ResponseMessages;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.dto.mapper.UserMapper;
import ru.ylab.tasks.task3.dto.request.user.RegisterRequest;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.model.User;
import ru.ylab.tasks.task3.util.validation.UserValidator;

import java.io.IOException;
import java.util.List;

import static ru.ylab.tasks.task3.constant.ResponseMessages.INVALID_JSON;
import static ru.ylab.tasks.task3.constant.ResponseMessages.VALIDATION_FAILED;

/**
 * Сервлет для регистрации новых пользователей в системе маркетплейса.
 * Обрабатывает POST запросы по пути "/marketplace/auth/register".
 * Создает новые учетные записи пользователей с указанными ролями.
 */
@WebServlet("/marketplace/auth/register")
public class RegisterServlet extends HttpServlet {

    private IUserController userController;
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Инициализирует сервлет, получая контроллер пользователей из контекста приложения.
     */
    @Override
    public void init() {
        this.userController = (IUserController) getServletContext().getAttribute("userController");
    }

    /**
     * Обрабатывает POST запрос на регистрацию нового пользователя.
     * Выполняет последовательно: парсинг DTO, валидацию данных, регистрацию пользователя
     * и отправку ответа с информацией о созданном пользователе.
     * @param req HTTP запрос
     * @param resp HTTP ответ
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        RegisterRequest dto = parseRegisterRequest(req, resp);
        if (dto == null) {
            return;
        }

        if (!validateRegistrationData(dto, resp)) {
            return;
        }

        User createdUser = registerUser(dto, resp);
        if (createdUser == null) {
            return;
        }

        sendSuccessResponse(createdUser, resp);
    }

    /**
     * Парсит DTO запроса на регистрацию из тела HTTP запроса.
     * @param req HTTP запрос
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return DTO запроса или null в случае ошибки парсинга
     */
    private RegisterRequest parseRegisterRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            return objectMapper.readValue(req.getInputStream(), RegisterRequest.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(INVALID_JSON, List.of(e.getMessage())));
            return null;
        }
    }

    /**
     * Валидирует данные для регистрации пользователя.
     * @param dto DTO запроса на регистрацию
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если данные валидны, false в противном случае
     */
    private boolean validateRegistrationData(RegisterRequest dto, HttpServletResponse resp) throws IOException {
        List<String> errors = UserValidator.validateRegister(dto);
        if (!errors.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.VALIDATION_FAILED, errors));
            return false;
        }
        return true;
    }

    /**
     * Выполняет регистрацию нового пользователя в системе.
     * @param dto DTO запроса на регистрацию
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return созданный пользователь или null в случае ошибки регистрации
     */
    private User registerUser(RegisterRequest dto, HttpServletResponse resp) throws IOException {
        boolean success = userController.register(dto.getLogin(), dto.getPassword(), dto.getRole());

        if (!success) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(VALIDATION_FAILED, List.of("Registration was not completed")));
            return null;
        }

        return userMapper.toEntity(dto);
    }

    /**
     * Отправляет успешный ответ с информацией о зарегистрированном пользователе.
     * @param createdUser созданная сущность пользователя
     * @param resp HTTP ответ
     */
    private void sendSuccessResponse(User createdUser, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), userMapper.toResponse(createdUser));
    }
}
