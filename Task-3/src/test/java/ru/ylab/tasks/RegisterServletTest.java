package ru.ylab.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.dto.request.user.RegisterRequest;
import ru.ylab.tasks.task3.servlet.user.RegisterServlet;
import ru.ylab.tasks.task3.util.validation.UserValidator;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServletTest {

    private RegisterServlet servlet;
    private ObjectMapper mapper = new ObjectMapper();
    private ByteArrayOutputStream outContent;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private ServletContext servletContext;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private IUserController userController;

    @BeforeEach
    void setup() throws Exception {
        servlet = new RegisterServlet();

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("userController")).thenReturn(userController);

        servlet.init(servletConfig);

        outContent = new ByteArrayOutputStream();
        when(resp.getWriter()).thenReturn(new PrintWriter(outContent, true));
    }

    @Test
    void testInvalidJson() throws Exception {
        when(req.getInputStream()).thenReturn(new MockServletInputStream("{ invalid json }"));

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outContent.toString().contains("Invalid JSON format"));
    }

    @Test
    void testValidationFailed() throws Exception {
        RegisterRequest dto = new RegisterRequest();
        dto.setLogin("");
        String json = mapper.writeValueAsString(dto);
        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));

        try (var mocked = mockStatic(UserValidator.class)) {
            mocked.when(() -> UserValidator.validateRegister(any())).thenReturn(List.of("Login is required"));

            servlet.doPost(req, resp);
        }

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outContent.toString().contains("Validation failed"));
    }

    @Test
    void testRegistrationNotCompleted() throws Exception {
        RegisterRequest dto = new RegisterRequest();
        dto.setLogin("user1");
        dto.setPassword("pass");
        dto.setRole("USER");

        String json = mapper.writeValueAsString(dto);
        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));
        when(userController.register("user1", "pass", "USER")).thenReturn(false);

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(outContent.toString().contains("Registration was not completed"));
    }

    @Test
    void testSuccessfulRegistration() throws Exception {
        RegisterRequest dto = new RegisterRequest();
        dto.setLogin("user2");
        dto.setPassword("pass");
        dto.setRole("USER");

        String json = mapper.writeValueAsString(dto);
        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));
        when(userController.register("user2", "pass", "USER")).thenReturn(true);

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        assertTrue(outContent.toString().contains("user2"));
    }

    @Test
    void testContentTypeAndEncoding() throws Exception {
        RegisterRequest dto = new RegisterRequest();
        dto.setLogin("user3");
        dto.setPassword("pass");
        dto.setRole("USER");

        String json = mapper.writeValueAsString(dto);
        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));
        when(userController.register(any(), any(), any())).thenReturn(true);

        servlet.doPost(req, resp);

        verify(resp).setContentType("application/json");
        verify(resp).setCharacterEncoding("UTF-8");
    }
}
