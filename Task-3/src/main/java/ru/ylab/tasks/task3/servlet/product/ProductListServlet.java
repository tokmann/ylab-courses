package ru.ylab.tasks.task3.servlet.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import ru.ylab.tasks.task3.constant.ResponseMessages;
import ru.ylab.tasks.task3.controller.ProductController;
import ru.ylab.tasks.task3.controller.UserController;
import ru.ylab.tasks.task3.controller.interfaces.IProductController;
import ru.ylab.tasks.task3.controller.interfaces.IUserController;
import ru.ylab.tasks.task3.dto.mapper.ProductMapper;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.product.ProductListResponse;
import ru.ylab.tasks.task3.dto.response.product.ProductResponse;
import ru.ylab.tasks.task3.model.Product;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static ru.ylab.tasks.task3.constant.ResponseMessages.USER_UNAUTHORIZED;

@WebServlet("/marketplace/products/list")
public class ProductListServlet extends HttpServlet {

    private IProductController productController;
    private IUserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @Override
    public void init() {
        this.productController =
                (IProductController) getServletContext().getAttribute("productController");
        this.userController = (IUserController) getServletContext().getAttribute("userController");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!userController.isAuthenticated()) {
            resp.setStatus(401);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(USER_UNAUTHORIZED, List.of("User must be logged in")));
            return;
        }

        List<Product> products;
        try {
            products = productController.getAllProducts();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.PRODUCT_SEARCH_FAILED, List.of(e.getMessage())));
            return;
        }

        List<ProductResponse> responseList = products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());

        resp.setStatus(200);
        objectMapper.writeValue(resp.getWriter(),
                new ProductListResponse(responseList));
    }
}
