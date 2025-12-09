package ru.ylab.tasks.task5.restcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ylab.tasks.task5.dto.mapper.UserMapper;
import ru.ylab.tasks.task5.dto.request.user.LoginRequest;
import ru.ylab.tasks.task5.dto.request.user.RegisterRequest;
import ru.ylab.tasks.task5.dto.response.user.LoginResponse;
import ru.ylab.tasks.task5.dto.response.user.LogoutResponse;
import ru.ylab.tasks.task5.model.User;
import ru.ylab.tasks.task5.security.AuthService;
import ru.ylab.tasks.task5.util.ResponseHelper;
import ru.ylab.tasks.task5.util.validation.UserValidator;

import java.util.List;

import static ru.ylab.tasks.task5.constant.ResponseMessages.VALIDATION_FAILED;
import static ru.ylab.tasks.task5.constant.ResponseMessages.USER_LOGIN_SUCCESS;
import static ru.ylab.tasks.task5.constant.ResponseMessages.USER_UNAUTHORIZED;
import static ru.ylab.tasks.task5.constant.ResponseMessages.USER_LOGOUT_SUCCESS;

/**
 * REST контроллер для управления аутентификацией и пользователями.
 * Обрабатывает запросы на регистрацию, вход и выход из системы.
 */
@RestController
@RequestMapping("/marketplace/auth")
public class UserRestController {

    private final AuthService authService;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final ResponseHelper responseHelper;

    public UserRestController(AuthService authService, UserMapper userMapper,
                              UserValidator userValidator, ResponseHelper responseHelper) {
        this.authService = authService;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
        this.responseHelper = responseHelper;
    }

    /**
     * Выполняет аутентификацию пользователя в системе.
     * @param request DTO с данными для входа (логин и пароль)
     * @return ResponseEntity с результатом операции
     */
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

    /**
     * Выполняет выход пользователя из системы.
     * Требует аутентификации пользователя.
     * @return ResponseEntity с результатом операции
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        if (!authService.isAuthenticated()) {
            return responseHelper.unauthorized(USER_UNAUTHORIZED, "User must be logged in");
        }

        authService.logout();
        return responseHelper.ok(new LogoutResponse(USER_LOGOUT_SUCCESS));
    }

    /**
     * Регистрирует нового пользователя в системе.
     * @param request DTO с данными для регистрации (логин, пароль, роль)
     * @return ResponseEntity с результатом операции
     */
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
