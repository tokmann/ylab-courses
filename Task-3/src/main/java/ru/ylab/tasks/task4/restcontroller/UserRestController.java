package ru.ylab.tasks.task4.restcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ylab.tasks.task4.dto.mapper.UserMapper;
import ru.ylab.tasks.task4.dto.request.user.LoginRequest;
import ru.ylab.tasks.task4.dto.request.user.RegisterRequest;
import ru.ylab.tasks.task4.dto.response.user.LoginResponse;
import ru.ylab.tasks.task4.dto.response.user.LogoutResponse;
import ru.ylab.tasks.task4.model.User;
import ru.ylab.tasks.task4.security.IAuthService;
import ru.ylab.tasks.task4.util.ResponseHelper;
import ru.ylab.tasks.task4.util.validation.UserValidator;

import java.util.List;

import static ru.ylab.tasks.task4.constant.ResponseMessages.*;

@RestController
@RequestMapping("/marketplace/auth")
public class UserRestController {

    private final IAuthService authService;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final ResponseHelper responseHelper;

    public UserRestController(IAuthService authService, UserMapper userMapper,
                              UserValidator userValidator, ResponseHelper responseHelper) {
        this.authService = authService;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
        this.responseHelper = responseHelper;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        List<String> errors = userValidator.validateLogin(request);
        if (!errors.isEmpty()) {
            return responseHelper.badRequest(VALIDATION_FAILED, String.valueOf(errors));
        }

        boolean success = authService.login(request.getLogin(), request.getPassword());

        if (!success) {
            return responseHelper.unauthorized(VALIDATION_FAILED, "Bad login or password");
        }

        return responseHelper.ok(new LoginResponse(USER_LOGIN_SUCCESS));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        if (!authService.isAuthenticated()) {
            return responseHelper.unauthorized(USER_UNAUTHORIZED, "User must be logged in");
        }

        authService.logout();
        return responseHelper.ok(new LogoutResponse(USER_LOGOUT_SUCCESS));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        List<String> errors = userValidator.validateRegister(request);
        if (!errors.isEmpty()) {
            return responseHelper.badRequest(VALIDATION_FAILED, String.valueOf(errors));
        }

        boolean success = authService.register(
                request.getLogin(),
                request.getPassword(),
                request.getRole()
        );

        if (!success) {
            return responseHelper.unauthorized(VALIDATION_FAILED, "Registration was not completed");
        }

        User created = userMapper.toEntity(request);
        return responseHelper.ok(userMapper.toResponse(created));
    }

}
