package ru.ylab.tasks.task3.servlet.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import ru.ylab.tasks.task3.constant.ResponseMessages;
import ru.ylab.tasks.task3.controller.ProductController;
import ru.ylab.tasks.task3.controller.UserController;
import ru.ylab.tasks.task3.dto.mapper.ProductMapper;
import ru.ylab.tasks.task3.dto.request.product.ProductUpdateRequest;
import ru.ylab.tasks.task3.dto.response.common.ErrorResponse;
import ru.ylab.tasks.task3.dto.response.product.ProductUpdatedResponse;
import ru.ylab.tasks.task3.exception.AccessDeniedException;
import ru.ylab.tasks.task3.util.validation.ProductValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static ru.ylab.tasks.task3.constant.ResponseMessages.*;

@WebServlet("/marketplace/products/update")
public class ProductUpdateServlet extends HttpServlet {

    private ProductController productController;
    private UserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        this.productController = (ProductController) getServletContext().getAttribute("productController");
        this.userController = (UserController) getServletContext().getAttribute("userController");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (!userController.isAuthenticated()) {
            resp.setStatus(401);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(USER_UNAUTHORIZED, List.of("User must be logged in")));
            return;
        }

        try {
            userController.checkAdmin(userController.currentUser());
        } catch (AccessDeniedException e) {
            resp.setStatus(403);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(USER_FORBIDDEN, List.of(e.getMessage())));
            return;
        }

        ProductUpdateRequest dto;
        try {
            dto = objectMapper.readValue(req.getInputStream(), ProductUpdateRequest.class);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(INVALID_JSON, List.of(e.getMessage())));
            return;
        }

        List<String> errors = ProductValidator.validateUpdate(dto);

        if (!errors.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(VALIDATION_FAILED, errors));
            return;
        }

        try {
            productController.updateProduct(
                    dto.getId(),
                    dto.getName(),
                    dto.getCategory(),
                    dto.getBrand(),
                    new BigDecimal(dto.getPrice()),
                    dto.getDescription()
            );
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                    new ErrorResponse(ResponseMessages.PRODUCT_CREATE_FAILED, List.of(e.getMessage())));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(),
                new ProductUpdatedResponse(dto.getId(), ResponseMessages.PRODUCT_UPDATED_SUCCESS));
    }

}
