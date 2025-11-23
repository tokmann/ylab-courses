package ru.ylab.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ylab.tasks.task3.controller.interfaces.IProductController;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.dto.request.product.ProductSearchRequest;
import ru.ylab.tasks.task3.model.Product;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.ylab.tasks.task3.servlet.product.ProductSearchServlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSearchServletTest {

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

    private ProductSearchServlet servlet;
    private ByteArrayOutputStream outContent;
    private PrintWriter writer;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() throws Exception {
        servlet = new ProductSearchServlet();

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
        assertTrue(outContent.toString().contains("Пользователь должен войти"));
    }

    @Test
    void testInvalidJson() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);

        when(req.getInputStream()).thenReturn(new MockServletInputStream("invalid json"));
        servlet.doPost(req, resp);

        verify(resp).setStatus(400);
        assertTrue(outContent.toString().contains("Invalid JSON format"));
    }

    @Test
    void testMinPriceGreaterThanMaxPrice() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);

        ProductSearchRequest requestDto = new ProductSearchRequest();
        requestDto.setMinPrice("100");
        requestDto.setMaxPrice("50");

        String json = mapper.writeValueAsString(requestDto);
        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));

        servlet.doPost(req, resp);

        verify(resp).setStatus(400);
        assertTrue(outContent.toString().contains("minPrice must be less than or equal to maxPrice"));
    }

    @Test
    void testSuccessfulSearch() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);

        ProductSearchRequest requestDto = new ProductSearchRequest();
        requestDto.setMinPrice("10");
        requestDto.setMaxPrice("1000");
        requestDto.setKeyword("test");
        requestDto.setBrand("brand");
        requestDto.setCategory("cat");

        String jsonString = mapper.writeValueAsString(requestDto);
        when(req.getInputStream()).thenReturn(new MockServletInputStream(jsonString));

        Product p1 = new Product(); p1.setId(1L); p1.setName("A");
        Product p2 = new Product(); p2.setId(2L); p2.setName("B");
        when(productController.searchProducts(any())).thenReturn(List.of(p1, p2));

        servlet.doPost(req, resp);

        verify(resp).setStatus(200);
        String json = outContent.toString();
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"id\":2"));
    }

    @Test
    void testControllerThrowsException() throws Exception {
        when(userController.isAuthenticated()).thenReturn(true);

        ProductSearchRequest requestDto = new ProductSearchRequest();

        String json = mapper.writeValueAsString(requestDto);
        when(req.getInputStream()).thenReturn(new MockServletInputStream(json));

        when(productController.searchProducts(any())).thenThrow(new RuntimeException("DB error"));

        servlet.doPost(req, resp);

        verify(resp).setStatus(500);
        assertTrue(outContent.toString().contains("Failed to search products"));
        assertTrue(outContent.toString().contains("DB error"));
    }
}
