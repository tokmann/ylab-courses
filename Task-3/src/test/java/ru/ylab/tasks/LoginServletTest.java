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
import ru.ylab.tasks.task3.dto.request.user.LoginRequest;
import ru.ylab.tasks.task3.servlet.user.LoginServlet;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServletTest {

    private LoginServlet servlet;
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
        servlet = new LoginServlet();

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("userController")).thenReturn(userController);

        servlet.init(servletConfig);

        outContent = new ByteArrayOutputStream();
        when(resp.getWriter()).thenReturn(new PrintWriter(outContent, true));
    }

    @Test
    void testInvalidJson() throws Exception {
        when(req.getInputStream()).thenReturn(new MockServletInputStream("invalid-json"));

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outContent.toString().contains("Invalid JSON format"));
    }

    @Test
    void testValidationFailed() throws Exception {
        LoginRequest dto = new LoginRequest();
        String json = mapper.writeValueAsString(dto);
        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outContent.toString().contains("Validation failed"));
    }

    @Test
    void testLoginFailed() throws Exception {
        LoginRequest dto = new LoginRequest();
        dto.setLogin("user");
        dto.setPassword("wrong");
        String json = mapper.writeValueAsString(dto);
        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));

        when(userController.login("user", "wrong")).thenReturn(false);

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(outContent.toString().contains("Bad login or password"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest dto = new LoginRequest();
        dto.setLogin("user");
        dto.setPassword("pass");
        String json = mapper.writeValueAsString(dto);
        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));

        when(userController.login("user", "pass")).thenReturn(true);

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
        assertTrue(outContent.toString().contains("User logged in successfully"));
    }

    @Test
    void testNullFieldsInRequest() throws Exception {
        LoginRequest dto = new LoginRequest();
        dto.setLogin(null);
        dto.setPassword(null);
        String json = mapper.writeValueAsString(dto);
        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(outContent.toString().contains("Validation failed"));
    }
}

