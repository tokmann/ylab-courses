package ru.ylab.tasks.task4.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.ylab.tasks.task4.constant.Role;
import ru.ylab.tasks.task4.dto.request.product.ProductCreateRequest;
import ru.ylab.tasks.task4.dto.request.product.ProductDeleteRequest;
import ru.ylab.tasks.task4.dto.request.product.ProductUpdateRequest;
import ru.ylab.tasks.task4.dto.request.user.LoginRequest;
import ru.ylab.tasks.task4.dto.request.user.RegisterRequest;
import ru.ylab.tasks.task4.dto.response.product.ProductDeletedResponse;
import ru.ylab.tasks.task4.dto.response.product.ProductUpdatedResponse;
import ru.ylab.tasks.task4.model.Product;
import ru.ylab.tasks.task4.security.IAuthService;
import ru.ylab.tasks.task4.service.audit.IAuditService;

import static ru.ylab.tasks.task4.constant.AuditMessages.*;

/**
 * Аспект для аудита действий пользователей в системе.
 * Логирует основные операции, выполняемые через контроллеры ProductController и UserController.
 */
@Aspect
@Component
public class AuditAspect {

    private final IAuditService auditService;
    private final IAuthService authService;

    public AuditAspect(IAuditService auditService, IAuthService authService) {
        this.auditService = auditService;
        this.authService = authService;
    }

    /**
     * Аудитует успешное добавление продукта.
     * Вызывается после успешного выполнения метода addProduct в ProductController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     */
    @AfterReturning("execution(* ru.ylab.tasks.task4.restcontroller.ProductRestController.createProduct(..))")
    public void auditAddProduct(JoinPoint jp) {
        ProductCreateRequest dto = (ProductCreateRequest) jp.getArgs()[0];
        auditService.log(String.format(PRODUCT_ADDED, authService.getCurrentUserLogin(), dto.getName()));
    }

    /**
     * Аудитует успешное обновление продукта.
     * Вызывается после успешного выполнения метода updateProduct в ProductController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     */
    @AfterReturning("execution(* ru.ylab.tasks.task4.restcontroller.ProductRestController.updateProduct(..))")
    public void auditUpdateProduct(JoinPoint jp) {
        ProductUpdateRequest response = (ProductUpdateRequest) jp.getArgs()[0];
        auditService.log(String.format(PRODUCT_UPDATED, authService.getCurrentUserLogin(), response.getId()));
    }

    /**
     * Аудитует успешное удаление продукта.
     * Вызывается после успешного выполнения метода deleteProduct в ProductController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     */
    @AfterReturning("execution(* ru.ylab.tasks.task4.restcontroller.ProductRestController.deleteProduct(..))")
    public void auditDeleteProduct(JoinPoint jp) {
        ProductDeleteRequest request = (ProductDeleteRequest) jp.getArgs()[0];
        auditService.log(String.format(PRODUCT_DELETED, authService.getCurrentUserLogin(), request.getId()));
    }

    /**
     * Аудитует выполнение поиска продуктов.
     * Вызывается после успешного выполнения метода searchProducts в ProductController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     */
    @AfterReturning("execution(* ru.ylab.tasks.task4.restcontroller.ProductRestController.searchProducts(..))")
    public void auditSearchProducts(JoinPoint jp) {
        auditService.log(String.format(PRODUCT_SEARCH, authService.getCurrentUserLogin()));
    }

    /**
     * Аудитует попытку входа пользователя в систему.
     * Вызывается после выполнения метода login в UserController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     */
    @AfterReturning(pointcut = "execution(* ru.ylab.tasks.task4.restcontroller.UserRestController.login(..))")
    public void auditLogin(JoinPoint jp) {
        LoginRequest dto = (LoginRequest) jp.getArgs()[0];
        boolean success = authService.isAuthenticated();
        auditService.log(success ?
                String.format(LOGIN_SUCCESS, dto.getLogin())
                : String.format(LOGIN_FAILED, dto.getLogin()));
    }

    /**
     * Аудитует выход пользователя из системы.
     * Вызывается перед выполнением метода logout в UserController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     */
    @Before("execution(* ru.ylab.tasks.task4.restcontroller.UserRestController.logout())")
    public void auditLogout(JoinPoint jp) {
        if (authService.isAuthenticated()) {
            auditService.log(String.format(LOGOUT_SUCCESS, authService.getCurrentUserLogin()));
        }
    }

    /**
     * Аудитует попытку регистрации пользователя.
     * Вызывается после выполнения метода register в UserController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     */
    @AfterReturning(pointcut = "execution(* ru.ylab.tasks.task4.restcontroller.UserRestController.register(..))")
    public void auditRegister(JoinPoint jp) {
        RegisterRequest dto = (RegisterRequest) jp.getArgs()[0];
        boolean success = authService.userExists(dto.getLogin());

        if (success) auditService.log(String.format(USER_REGISTERED, dto.getLogin(), dto.getRole()));
        else auditService.log(String.format(USER_REGISTER_FAILED, dto.getLogin()));
    }
}