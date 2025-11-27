package ru.ylab.tasks;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ylab.tasks.task3.controller.interfaces.IProductController;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.exception.AccessDeniedException;
import ru.ylab.tasks.task3.model.User;
import ru.ylab.tasks.task3.servlet.product.ProductDeleteServlet;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductDeleteServletTest {

    @Mock
    HttpServletRequest req;

    @Mock
    HttpServletResponse resp;

    @Mock
    ServletContext ctx;

    @Mock
    IProductController productController;

    @Mock
    IUserController userController;

    @Mock
    private ServletConfig servletConfig;

    private ProductDeleteServlet servlet;
    private ByteArrayOutputStream outContent;
    private PrintWriter writer;

    @BeforeEach
    void setup() throws Exception {
        servlet = new ProductDeleteServlet();

        outContent = new ByteArrayOutputStream();
        writer = new PrintWriter(outContent, true);
        when(resp.getWriter()).thenReturn(writer);

        when(servletConfig.getServletContext()).thenReturn(ctx);

        when(ctx.getAttribute("productController")).thenReturn(productController);
        when(ctx.getAttribute("userController")).thenReturn(userController);

        servlet.init(servletConfig);
    }

    @Test
    void testUnauthorized() throws Exception {
        when(userController.isAuthenticated()).thenReturn(false);

        servlet.doPost(req, resp);

        verify(resp).setStatus(401);
        assertTrue(outContent.toString().contains("User must be logged in"));
    }

    @Test
    void testForbidden() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);
        when(userController.currentUser()).thenReturn(new User());
        doThrow(new AccessDeniedException("Access denied"))
                .when(userController).checkAdmin(any());

        servlet.doPost(req, resp);

        verify(resp).setStatus(403);
        assertTrue(outContent.toString().contains("Access denied"));
    }

    @Test
    void testInvalidJson() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);
        when(userController.currentUser()).thenReturn(new User());

        when(req.getInputStream()).thenReturn(new MockServletInputStream("invalid json"));

        servlet.doPost(req, resp);

        verify(resp).setStatus(400);
        assertTrue(outContent.toString().contains("Invalid JSON format"));
    }

    @Test
    void testIdMissing() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);
        when(userController.currentUser()).thenReturn(new User());

        String json = """
            {"id": null}
        """;

        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));

        servlet.doPost(req, resp);

        verify(resp).setStatus(400);
        assertTrue(outContent.toString().contains("Product id must be provided"));
    }

    @Test
    void testSuccess() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);
        when(userController.currentUser()).thenReturn(new User());

        String json = """
            {"id": 42}
        """;

        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));

        doNothing().when(productController).deleteProduct(42L);

        servlet.doPost(req, resp);

        verify(resp).setStatus(200);
        assertTrue(outContent.toString().contains("\"productId\":42"));
        assertTrue(outContent.toString().contains("Product deleted successfully"));
    }

}
