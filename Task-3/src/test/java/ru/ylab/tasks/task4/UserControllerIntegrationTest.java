package ru.ylab.tasks.task4;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import ru.ylab.tasks.task4.config.LiquibaseConfiguration;
import ru.ylab.tasks.task4.constant.Role;
import ru.ylab.tasks.task4.dto.request.user.LoginRequest;
import ru.ylab.tasks.task4.dto.request.user.RegisterRequest;
import ru.ylab.tasks.task4.model.User;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig(classes = {TestIntegrationConfiguration.class, LiquibaseConfiguration.class})
class UserControllerIntegrationTest extends AbstractIntegrationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        super.setUp();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    void registerFirst_ShouldCreateAdmin_WhenValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setLogin("admin");
        request.setPassword("admin");
        request.setRole("ADMIN");

        mockMvc.perform(post("/marketplace/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("admin"));

        Optional<User> savedUser = userRepository.findByLogin("admin");
        assertTrue(savedUser.isPresent());
        assertEquals("admin", savedUser.get().getLogin());
        assertEquals(Role.ADMIN, savedUser.get().getRole());
    }

    @Test
    void login_ShouldReturnSuccess_WhenValidCredentials() throws Exception {
        createTestUser("testuser", "password123", Role.USER);

        LoginRequest request = new LoginRequest();
        request.setLogin("testuser");
        request.setPassword("password123");

        mockMvc.perform(post("/marketplace/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged in successfully"));

        assertTrue(authService.isAuthenticated());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLogin("nonexistent");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/marketplace/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        assertFalse(authService.isAuthenticated());
    }

    @Test
    void logout_ShouldReturnSuccess_WhenLoggedIn() throws Exception {
        createTestUser("logoutuser", "password123", Role.USER);
        authService.login("logoutuser", "password123");

        mockMvc.perform(post("/marketplace/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged out successfully"));

        assertFalse(authService.isAuthenticated());
    }

    @Test
    void logout_ShouldReturnUnauthorized_WhenNotLoggedIn() throws Exception {
        mockMvc.perform(post("/marketplace/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_ShouldCreateAdminUser_WhenAdminRole() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setLogin("adminuser");
        request.setPassword("adminpass");
        request.setRole("ADMIN");

        mockMvc.perform(post("/marketplace/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("adminuser"));

        Optional<User> savedUser = userRepository.findByLogin("adminuser");
        assertTrue(savedUser.isPresent());
        assertEquals(Role.ADMIN, savedUser.get().getRole());
    }
}
