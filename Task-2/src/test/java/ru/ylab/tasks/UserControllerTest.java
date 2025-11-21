package ru.ylab.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.tasks.task2.constant.Role;
import ru.ylab.tasks.task2.controller.UserController;
import ru.ylab.tasks.task2.repository.jdbc.JdbcUserRepository;
import ru.ylab.tasks.task2.security.AuthService;
import ru.ylab.tasks.task2.service.AuditService;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest {

    private UserController userController;
    private JdbcUserRepository userRepository;

    @BeforeEach
    void setUp() {
        TestDatabase.clearData();
        userRepository = new JdbcUserRepository(TestDatabase.getConnection());
        AuthService authService = new AuthService(userRepository);
        AuditService auditService = new AuditService();
        userController = new UserController(authService, auditService);
    }

    @Test
    void login_shouldReturnTrueWithValidCredentials() {
        userController.register("testuser", "password", "USER");

        boolean result = userController.login("testuser", "password");

        assertThat(result).isTrue();
        assertThat(userController.isAuthenticated()).isTrue();
    }

    @Test
    void login_shouldReturnFalseWithInvalidCredentials() {
        userController.register("testuser", "password", "USER");

        boolean result = userController.login("testuser", "wrongpass");

        assertThat(result).isFalse();
        assertThat(userController.isAuthenticated()).isFalse();
    }

    @Test
    void logout_shouldClearAuthentication() {
        userController.register("testuser", "password", "USER");
        userController.login("testuser", "password");

        userController.logout();

        assertThat(userController.isAuthenticated()).isFalse();
    }

    @Test
    void register_shouldCreateNewUser() {
        boolean result = userController.register("newuser", "password", "USER");

        assertThat(result).isTrue();

        var found = userRepository.findByLogin("newuser");
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void register_shouldReturnFalseForDuplicateLogin() {
        userController.register("existing", "pass1", "USER");

        boolean result = userController.register("existing", "pass2", "USER");

        assertThat(result).isFalse();
    }

    @Test
    void currentUser_shouldReturnLoggedInUser() {
        userController.register("testuser", "password", "USER");
        userController.login("testuser", "password");

        var currentUser = userController.currentUser();

        assertThat(currentUser).isNotNull();
        assertThat(currentUser.getLogin()).isEqualTo("testuser");
    }

    @Test
    void currentUser_shouldReturnNullWhenNotAuthenticated() {
        var currentUser = userController.currentUser();

        assertThat(currentUser).isNull();
    }
}
