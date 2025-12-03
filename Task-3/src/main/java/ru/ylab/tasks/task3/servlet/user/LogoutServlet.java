package ru.ylab.tasks.task3.servlet.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.user.LogoutResponse;

import java.io.IOException;
import java.util.List;

import static ru.ylab.tasks.task3.constant.ResponseMessages.*;

/**
 * Сервлет для выхода пользователей из системы маркетплейса.
 * Обрабатывает POST запросы по пути "/marketplace/auth/logout".
 * Завершает текущую сессию пользователя и очищает данные аутентификации.
 */
@WebServlet("/marketplace/auth/logout")
public class LogoutServlet extends HttpServlet {

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
     * Обрабатывает POST запрос на выход пользователя из системы.
     * Выполняет последовательно: проверку аутентификации, выход из системы
     * и отправку подтверждающего ответа.
     * @param req HTTP запрос
     * @param resp HTTP ответ
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!checkAuthentication(resp)) {
            return;
        }

        performLogout();

        sendSuccessResponse(resp);
    }

    /**
     * Проверяет, аутентифицирован ли текущий пользователь.
     * @param resp HTTP ответ для отправки ошибки в случае неудачи
     * @return true если пользователь аутентифицирован, false в противном случае
     */
    private boolean checkAuthentication(HttpServletResponse resp) throws IOException {
        if (!userController.isAuthenticated()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(USER_UNAUTHORIZED, List.of("User must be logged in")));
            return false;
        }
        return true;
    }

    /**
     * Выполняет операцию выхода пользователя из системы.
     * Очищает данные аутентификации и завершает сессию.
     */
    private void performLogout() {
        userController.logout();
    }

    /**
     * Отправляет успешный ответ о выходе из системы.
     * @param resp HTTP ответ
     */
    private void sendSuccessResponse(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), new LogoutResponse(USER_LOGOUT_SUCCESS));
    }
}
