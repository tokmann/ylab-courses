package ru.ylab.tasks.task5;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import ru.ylab.tasks.task5.constant.Role;
import ru.ylab.tasks.task5.dto.request.user.LoginRequest;
import ru.ylab.tasks.task5.dto.request.user.RegisterRequest;
import ru.ylab.tasks.task5.model.User;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("Регистрация первого пользователя: должен создать администратора при корректном запросе")
    void registerFirst_ShouldCreateAdmin_WhenValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setLogin("admin");
        request.setPassword("admin");
        request.setRole("ADMIN");

        SoftAssertions softly = new SoftAssertions();

        mockMvc.perform(post("/marketplace/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("admin"));

        Optional<User> savedUser = userRepository.findByLogin("admin");

        softly.assertThat(savedUser).isPresent();
        softly.assertThat(savedUser.get().getLogin()).isEqualTo("admin");
        softly.assertThat(savedUser.get().getRole()).isEqualTo(Role.ADMIN);

        softly.assertAll();
    }

    @Test
    @DisplayName("Авторизация: должен вернуть успешный результат при корректных учетных данных")
    void login_ShouldReturnSuccess_WhenValidCredentials() throws Exception {
        createTestUser("testuser", "password123", Role.USER);
        LoginRequest request = new LoginRequest();
        request.setLogin("testuser");
        request.setPassword("password123");

        SoftAssertions softly = new SoftAssertions();

        mockMvc.perform(post("/marketplace/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged in successfully"));

        softly.assertThat(authService.isAuthenticated()).isTrue();

        softly.assertAll();
    }

    @Test
    @DisplayName("Авторизация: должен вернуть Unauthorized при неверных учетных данных")
    void login_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLogin("nonexistent");
        request.setPassword("wrongpassword");

        SoftAssertions softly = new SoftAssertions();

        mockMvc.perform(post("/marketplace/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        softly.assertThat(authService.isAuthenticated()).isFalse();

        softly.assertAll();
    }

    @Test
    @DisplayName("Выход из системы: должен вернуть успешный результат при выходе авторизованного пользователя")
    void logout_ShouldReturnSuccess_WhenLoggedIn() throws Exception {
        createTestUser("logoutuser", "password123", Role.USER);
        authService.login("logoutuser", "password123");

        SoftAssertions softly = new SoftAssertions();

        mockMvc.perform(post("/marketplace/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged out successfully"));

        softly.assertThat(authService.isAuthenticated()).isFalse();

        softly.assertAll();
    }

    @Test
    @DisplayName("Выход из системы: должен вернуть Unauthorized при попытке выхода неавторизованного пользователя")
    void logout_ShouldReturnUnauthorized_WhenNotLoggedIn() throws Exception {
        mockMvc.perform(post("/marketplace/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Регистрация: должен создать пользователя с ролью ADMIN при указании роли администратора")
    void register_ShouldCreateAdminUser_WhenAdminRole() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setLogin("adminuser");
        request.setPassword("adminpass");
        request.setRole("ADMIN");

        SoftAssertions softly = new SoftAssertions();

        mockMvc.perform(post("/marketplace/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("adminuser"));

        Optional<User> savedUser = userRepository.findByLogin("adminuser");

        softly.assertThat(savedUser).isPresent();
        softly.assertThat(savedUser.get().getRole()).isEqualTo(Role.ADMIN);

        softly.assertAll();
    }
}