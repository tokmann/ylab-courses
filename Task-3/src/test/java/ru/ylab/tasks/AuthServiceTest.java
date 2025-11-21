package ru.ylab.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ylab.tasks.task3.constant.Role;
import ru.ylab.tasks.task3.exception.AccessDeniedException;
import ru.ylab.tasks.task3.model.User;
import ru.ylab.tasks.task3.repository.jdbc.JdbcUserRepository;
import ru.ylab.tasks.task3.security.AuthService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthServiceTest {

    private AuthService authService;
    private JdbcUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        TestDatabase.clearData();
        userRepository = new JdbcUserRepository(TestDatabase.getConnection());
        authService = new AuthService(userRepository);
    }

    @Test
    public void login_shouldReturnTrueWithValidCredentials() {
        User user = new User("testuser", "password", Role.USER);
        userRepository.save(user);

        boolean result = authService.login("testuser", "password");

        assertThat(result).isTrue();
        assertThat(authService.isAuthenticated()).isTrue();
        assertThat(authService.getCurrentUserLogin()).isEqualTo("testuser");
    }

    @Test
    public void login_shouldReturnFalseWithInvalidPassword() {
        User user = new User("testuser", "password", Role.USER);
        userRepository.save(user);

        boolean result = authService.login("testuser", "wrongpassword");

        assertThat(result).isFalse();
        assertThat(authService.isAuthenticated()).isFalse();
    }

    @Test
    public void login_shouldReturnFalseWithNonExistentUser() {
        boolean result = authService.login("nonexistent", "password");

        assertThat(result).isFalse();
        assertThat(authService.isAuthenticated()).isFalse();
    }

    @Test
    public void logout_shouldClearCurrentUser() {
        User user = new User("testuser", "password", Role.USER);
        userRepository.save(user);
        authService.login("testuser", "password");

        authService.logout();

        assertThat(authService.isAuthenticated()).isFalse();
        assertThat(authService.getCurrentUser()).isNull();
    }

    @Test
    public void register_shouldCreateNewADMIN() {
        boolean result = authService.register("newuser", "password", "USER");

        assertThat(result).isTrue();

        var found = userRepository.findByLogin("newuser");
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    public void register_shouldAssignAdminRoleForFirstUser() {
        boolean result = authService.register("firstuser", "password", "USER");

        assertThat(result).isTrue();

        var found = userRepository.findByLogin("firstuser");
        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    public void register_shouldReturnFalseForDuplicateLogin() {
        authService.register("existing", "pass1", "USER");

        boolean result = authService.register("existing", "pass2", "USER");

        assertThat(result).isFalse();
    }

    @Test
    public void determineAssignedRole_shouldReturnAdminForFirstUser() {
        Role role = authService.determineAssignedRole("USER");

        assertThat(role).isEqualTo(Role.ADMIN);
    }

    @Test
    public void determineAssignedRole_shouldReturnUserForSubsequentUsers() {
        authService.register("admin", "pass", "ADMIN");

        Role role = authService.determineAssignedRole("USER");

        assertThat(role).isEqualTo(Role.USER);
    }

    @Test
    public void determineAssignedRole_shouldReturnRequestedRoleWhenValid() {
        authService.register("admin", "pass", "ADMIN");

        Role role = authService.determineAssignedRole("ADMIN");

        assertThat(role).isEqualTo(Role.ADMIN);
    }

    @Test
    public void determineAssignedRole_shouldReturnUserWhenRequestedRoleInvalid() {
        authService.register("admin", "pass", "ADMIN");

        Role role = authService.determineAssignedRole("INVALID_ROLE");

        assertThat(role).isEqualTo(Role.USER);
    }

    @Test
    public void checkAdmin_shouldNotThrowForAdminUser() {
        User admin = new User("admin", "pass", Role.ADMIN);

        authService.checkAdmin(admin);
    }

    @Test
    public void checkAdmin_shouldThrowForNonAdminUser() {
        User user = new User("user", "pass", Role.USER);

        assertThrows(AccessDeniedException.class, () -> authService.checkAdmin(user));
    }
}
