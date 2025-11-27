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
import ru.ylab.tasks.task3.servlet.user.LogoutServlet;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServletTest {

    private LogoutServlet servlet;
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
        servlet = new LogoutServlet();

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("userController")).thenReturn(userController);

        servlet.init(servletConfig);

        outContent = new ByteArrayOutputStream();
        when(resp.getWriter()).thenReturn(new PrintWriter(outContent, true));
    }

    @Test
    void testUserNotAuthenticated() throws Exception {
        when(userController.isAuthenticated()).thenReturn(false);

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(outContent.toString().contains("You are not authorized"));
    }

    @Test
    void testLogoutSuccess() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);

        servlet.doPost(req, resp);

        verify(userController).logout();
        verify(resp).setStatus(HttpServletResponse.SC_OK);
        assertTrue(outContent.toString().contains("User logged out successfully"));
    }

    @Test
    void testMultipleLogoutCalls() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);

        servlet.doPost(req, resp);
        servlet.doPost(req, resp);

        verify(userController, times(2)).logout();
    }

    @Test
    void testOutputJsonStructure() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);

        servlet.doPost(req, resp);

        String json = outContent.toString();
        assertTrue(json.contains("User logged out successfully"));
        assertTrue(json.startsWith("{") && json.endsWith("}"));
    }

    @Test
    void testContentTypeAndEncoding() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);

        servlet.doPost(req, resp);

        verify(resp).setContentType("application/json");
        verify(resp).setCharacterEncoding("UTF-8");
    }
}

