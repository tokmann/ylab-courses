package ru.ylab.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ylab.tasks.task3.controller.interfaces.IProductController;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.dto.response.product.ProductListResponse;
import ru.ylab.tasks.task3.model.Product;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.tasks.task3.servlet.product.ProductListServlet;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductListServletTest {

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @Mock
    private ServletContext ctx;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private IProductController productController;

    @Mock
    private IUserController userController;

    private ProductListServlet servlet;
    private ByteArrayOutputStream outContent;
    private PrintWriter writer;

    @BeforeEach
    void setup() throws Exception {
        servlet = new ProductListServlet();

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

        servlet.doGet(req, resp);

        verify(resp).setStatus(401);
        assertTrue(outContent.toString().contains("User must be logged in"));
    }

    @Test
    void testControllerThrowsException() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);
        when(productController.getAllProducts()).thenThrow(new RuntimeException("DB error"));

        servlet.doGet(req, resp);

        verify(resp).setStatus(500);
        assertTrue(outContent.toString().contains("DB error"));
    }

    @Test
    void testEmptyProductList() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);
        when(productController.getAllProducts()).thenReturn(List.of());

        servlet.doGet(req, resp);

        verify(resp).setStatus(200);
        ObjectMapper mapper = new ObjectMapper();
        ProductListResponse response = mapper.readValue(outContent.toString(), ProductListResponse.class);
        assertTrue(response.getProducts().isEmpty());
    }

    @Test
    void testNonEmptyProductList() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);

        Product p1 = new Product(); p1.setId(1L); p1.setName("A");
        Product p2 = new Product(); p2.setId(2L); p2.setName("B");

        when(productController.getAllProducts()).thenReturn(List.of(p1, p2));

        servlet.doGet(req, resp);

        verify(resp).setStatus(200);
        String json = outContent.toString();
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"id\":2"));
    }

}
