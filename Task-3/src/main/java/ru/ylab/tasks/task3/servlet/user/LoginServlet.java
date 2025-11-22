package ru.ylab.tasks.task3.servlet.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.tasks.task3.constant.ResponseMessages;
import ru.ylab.tasks.task3.controller.UserController;
import ru.ylab.tasks.task3.dto.request.user.LoginRequest;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.user.LoginResponse;
import ru.ylab.tasks.task3.util.validation.UserValidator;

import java.io.IOException;
import java.util.List;

import static ru.ylab.tasks.task3.constant.ResponseMessages.*;

@WebServlet("/marketplace/auth/login")
public class LoginServlet extends HttpServlet  {

    private UserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        this.userController = (UserController) getServletContext().getAttribute("userController");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        LoginRequest dto;
        try {
            dto = objectMapper.readValue(req.getInputStream(), LoginRequest.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(INVALID_JSON, List.of(e.getMessage())));
            return;
        }

        List<String> errors = UserValidator.validateLogin(dto);
        if (!errors.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.VALIDATION_FAILED, errors));
            return;
        }

        boolean success = userController.login(dto.getLogin(), dto.getPassword());

        if (!success) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(VALIDATION_FAILED, List.of("Неверный логин или пароль")));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(),
                new LoginResponse(USER_LOGIN_SUCCESS));
    }
}
