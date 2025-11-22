package ru.ylab.tasks.task3.servlet.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.tasks.task3.controller.UserController;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.user.LogoutResponse;

import java.io.IOException;
import java.util.List;

import static ru.ylab.tasks.task3.constant.ResponseMessages.*;

@WebServlet("/marketplace/auth/logout")
public class LogoutServlet extends HttpServlet {

    private UserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        this.userController = (UserController) getServletContext().getAttribute("userController");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!userController.isAuthenticated()) {
            resp.setStatus(401);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(USER_UNAUTHORIZED, List.of("Пользователь должен войти")));
            return;
        }

        userController.logout();

        resp.setStatus(200);
        objectMapper.writeValue(resp.getWriter(),
                new LogoutResponse(USER_LOGOUT_SUCCESS));
    }
}
