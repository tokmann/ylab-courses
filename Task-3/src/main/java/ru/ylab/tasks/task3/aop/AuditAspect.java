package ru.ylab.tasks.task3.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import ru.ylab.tasks.task3.constant.Role;
import ru.ylab.tasks.task3.model.Product;
import ru.ylab.tasks.task3.security.IAuthService;
import ru.ylab.tasks.task3.service.audit.IAuditService;

import static ru.ylab.tasks.task3.constant.AuditMessages.*;

/**
 * Аспект для аудита действий пользователей в системе.
 * Логирует основные операции, выполняемые через контроллеры ProductController и UserController.
 */
@Aspect
public class AuditAspect {

    private static IAuditService auditService;
    private static IAuthService authService;

    public static void setAuditService(IAuditService service) {
        auditService = service;
    }

    public static void setAuthService(IAuthService service) {
        authService = service;
    }

    /**
     * Аудитует успешное добавление продукта.
     * Вызывается после успешного выполнения метода addProduct в ProductController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     */
    @AfterReturning("execution(* ru.ylab.tasks.task3.controller.ProductController.addProduct(..))")
    public void auditAddProduct(JoinPoint jp) {
        Product product = (Product) jp.getArgs()[0];
        auditService.log(String.format(PRODUCT_ADDED, product.getName(), authService.getCurrentUserLogin()));
    }

    /**
     * Аудитует успешное обновление продукта.
     * Вызывается после успешного выполнения метода updateProduct в ProductController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     */
    @AfterReturning("execution(* ru.ylab.tasks.task3.controller.ProductController.updateProduct(..))")
    public void auditUpdateProduct(JoinPoint jp) {
        Long productId = (Long) jp.getArgs()[0];
        auditService.log(String.format(PRODUCT_UPDATED, productId, authService.getCurrentUserLogin()));
    }

    /**
     * Аудитует успешное удаление продукта.
     * Вызывается после успешного выполнения метода deleteProduct в ProductController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     */
    @AfterReturning("execution(* ru.ylab.tasks.task3.controller.ProductController.deleteProduct(..))")
    public void auditDeleteProduct(JoinPoint jp) {
        Long productId = (Long) jp.getArgs()[0];
        auditService.log(String.format(PRODUCT_DELETED, productId, authService.getCurrentUserLogin()));
    }

    /**
     * Аудитует выполнение поиска продуктов.
     * Вызывается после успешного выполнения метода searchProducts в ProductController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     */
    @AfterReturning("execution(* ru.ylab.tasks.task3.controller.ProductController.searchProducts(..))")
    public void auditSearchProducts(JoinPoint jp) {
        auditService.log(String.format(PRODUCT_SEARCH, authService.getCurrentUserLogin()));
    }

    /**
     * Аудитует попытку входа пользователя в систему.
     * Вызывается после выполнения метода login в UserController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     * @param success результат попытки входа (true - успешно, false - неудачно)
     */
    @AfterReturning(pointcut = "execution(* ru.ylab.tasks.task3.controller.UserController.login(..))", returning = "success")
    public void auditLogin(JoinPoint jp, boolean success) {
        String login = (String) jp.getArgs()[0];
        if (success) {
            auditService.log(String.format(LOGIN_SUCCESS, login));
        } else {
            auditService.log(String.format(LOGIN_FAILED, login));
        }
    }

    /**
     * Аудитует выход пользователя из системы.
     * Вызывается перед выполнением метода logout в UserController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     */
    @Before("execution(* ru.ylab.tasks.task3.controller.UserController.logout())")
    public void auditLogout(JoinPoint jp) {
        if (authService.isAuthenticated()) {
            auditService.log(String.format(LOGOUT_SUCCESS, authService.getCurrentUserLogin()));
        }
    }

    /**
     * Аудитует попытку регистрации пользователя.
     * Вызывается после выполнения метода register в UserController.
     * @param jp точка соединения, содержащая информацию о выполняемом методе
     * @param success результат попытки регистрации (true - успешно, false - неудачно)
     */
    @AfterReturning(pointcut = "execution(* ru.ylab.tasks.task3.controller.UserController.register(..))", returning = "success")
    public void auditRegister(JoinPoint jp, boolean success) {
        String login = (String) jp.getArgs()[0];
        String requestedRole = (String) jp.getArgs()[2];
        Role assignedRole = authService.determineAssignedRole(requestedRole);

        if (success) {
            auditService.log(String.format(USER_REGISTERED, login, assignedRole));
        } else {
            auditService.log(String.format(USER_REGISTER_FAILED, login));
        }
    }
}