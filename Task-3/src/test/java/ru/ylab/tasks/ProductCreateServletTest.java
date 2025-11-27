package ru.ylab.tasks;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ylab.tasks.task3.controller.interfaces.IProductController;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.exception.AccessDeniedException;
import ru.ylab.tasks.task3.model.Product;
import ru.ylab.tasks.task3.model.User;
import ru.ylab.tasks.task3.servlet.product.ProductCreateServlet;
import ru.ylab.tasks.task3.util.validation.ProductValidator;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCreateServletTest {

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

    private ProductCreateServlet servlet;
    private ByteArrayOutputStream outContent;
    private PrintWriter writer;

    @BeforeEach
    void setup() throws Exception {
        servlet = new ProductCreateServlet();

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
    void testValidationFailed() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);
        when(userController.currentUser()).thenReturn(new User());

        String json = """
            {"name": "", "price": -5}
        """;

        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));

        //Подменяем валидатор
        try (MockedStatic<ProductValidator> validatorMock = Mockito.mockStatic(ProductValidator.class)) {
            validatorMock.when(() -> ProductValidator.validateCreate(any()))
                    .thenReturn(List.of("Invalid name", "Invalid price"));

            servlet.doPost(req, resp);

            verify(resp).setStatus(400);
            assertTrue(outContent.toString().contains("Invalid name"));
        }
    }

    @Test
    void testSuccess() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);
        when(userController.currentUser()).thenReturn(new User());

        String json = """
            {
                "name": "Phone",
                "category": "Electronics",
                "brand": "BrandX",
                "price": 999,
                "description": "Nice phone"
            }
        """;

        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));

        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(10L);
            return null;
        }).when(productController).addProduct(any());

        servlet.doPost(req, resp);

        verify(resp).setStatus(201);
        assertTrue(outContent.toString().contains("\"productId\":10"));
    }

}