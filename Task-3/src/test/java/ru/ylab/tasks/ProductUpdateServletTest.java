package ru.ylab.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ylab.tasks.task3.controller.interfaces.IProductController;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;


import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.tasks.task3.dto.request.product.ProductUpdateRequest;
import ru.ylab.tasks.task3.exception.AccessDeniedException;
import ru.ylab.tasks.task3.servlet.product.ProductUpdateServlet;
import ru.ylab.tasks.task3.util.validation.ProductValidator;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductUpdateServletTest {

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private ServletContext ctx;

    @Mock
    private IProductController productController;

    @Mock
    private IUserController userController;

    private ProductUpdateServlet servlet;
    private ByteArrayOutputStream outContent;
    private PrintWriter writer;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() throws Exception {
        servlet = new ProductUpdateServlet();

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
    void testUserNotAdmin() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);
        doThrow(new AccessDeniedException("Not admin")).when(userController).checkAdmin(any());

        servlet.doPost(req, resp);

        verify(resp).setStatus(403);
        assertTrue(outContent.toString().contains("Not admin"));
    }

    @Test
    void testInvalidJson() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);
        when(req.getInputStream()).thenReturn(
                new MockServletInputStream("invalid json")
        );

        servlet.doPost(req, resp);

        verify(resp).setStatus(400);
        assertTrue(outContent.toString().contains("Invalid JSON format"));
    }


    @Test
    void testValidationFailed() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);

        ProductUpdateRequest dto = new ProductUpdateRequest();
        when(req.getInputStream()).thenReturn(new MockServletInputStream(mapper.writeValueAsString(dto)));

        try (var mocked = mockStatic(ProductValidator.class)) {
            mocked.when(() -> ProductValidator.validateUpdate(any())).thenReturn(List.of("error"));

            servlet.doPost(req, resp);

            verify(resp).setStatus(400);
            assertTrue(outContent.toString().contains("Validation failed"));
        }
    }

    @Test
    void testSuccessfulUpdate() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);

        ProductUpdateRequest dto = new ProductUpdateRequest();
        dto.setId(1L);
        dto.setName("Name");
        dto.setCategory("Cat");
        dto.setBrand("Brand");
        dto.setPrice("100");
        dto.setDescription("Desc");

        when(req.getInputStream()).thenReturn(new MockServletInputStream(mapper.writeValueAsString(dto)));
        try (var mocked = mockStatic(ProductValidator.class)) {
            mocked.when(() -> ProductValidator.validateUpdate(any())).thenReturn(List.of());

            servlet.doPost(req, resp);

            verify(resp).setStatus(200);
            String json = outContent.toString();
            assertTrue(json.contains("\"productId\":1"));
            assertTrue(json.contains("Product listed successfully"));
        }
    }

    @Test
    void testControllerThrowsException() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);

        ProductUpdateRequest dto = new ProductUpdateRequest();
        dto.setId(1L);
        String json = mapper.writeValueAsString(dto);
        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));
        try (var mocked = mockStatic(ProductValidator.class)) {
            mocked.when(() -> ProductValidator.validateUpdate(any())).thenReturn(List.of());

            servlet.doPost(req, resp);

            verify(resp).setStatus(500);
            assertTrue(outContent.toString().contains("Failed to create product"));
        }
    }
}
