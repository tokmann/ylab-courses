package ru.ylab.tasks.task3.servlet.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import ru.ylab.tasks.task3.constant.ResponseMessages;
import ru.ylab.tasks.task3.controller.UserController;
import ru.ylab.tasks.task3.dto.mapper.UserMapper;
import ru.ylab.tasks.task3.dto.request.user.RegisterRequest;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.user.RegisterResponse;
import ru.ylab.tasks.task3.model.User;
import ru.ylab.tasks.task3.util.validation.UserValidator;

import java.io.IOException;
import java.util.List;

import static ru.ylab.tasks.task3.constant.ResponseMessages.INVALID_JSON;
import static ru.ylab.tasks.task3.constant.ResponseMessages.VALIDATION_FAILED;

@WebServlet("/marketplace/auth/register")
public class RegisterServlet extends HttpServlet {

    private UserController userController;
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        this.userController =
                (UserController) getServletContext().getAttribute("userController");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        RegisterRequest dto;
        try {
            dto = objectMapper.readValue(req.getInputStream(), RegisterRequest.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(INVALID_JSON, List.of(e.getMessage())));
            return;
        }

        List<String> errors = UserValidator.validateRegister(dto);
        if (!errors.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.VALIDATION_FAILED, errors));
            return;
        }

        boolean success = userController.register(dto.getLogin(), dto.getPassword(), dto.getRole());

        if (!success) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(VALIDATION_FAILED, List.of("Регистрация не завершилась")));
            return;
        }

        User created = userMapper.toEntity(dto);

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(),
                userMapper.toResponse(created));
    }
}
