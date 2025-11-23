package ru.ylab.tasks.task3.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Аспект для измерения времени выполнения методов контроллеров.
 * Замеряет и логирует время выполнения методов ProductController и UserController.
 */
@Aspect
public class TimingAspect {

    /**
     * Точка среза, определяющая все методы в ProductController и UserController.
     */
    @Pointcut("execution(* ru.ylab.tasks.task3.controller.ProductController.*(..)) || "+
                     "execution(* ru.ylab.tasks.task3.controller.UserController.*(..))")
    public void allControllerMethods() {
    }

    /**
     * Измеряет время выполнения методов контроллеров.
     * Логирует начало и окончание выполнения метода, а также затраченное время.
     * @param proceedingJoinPoint точка соединения для продолжения выполнения метода
     * @return результат выполнения целевого метода
     */
    @Around("allControllerMethods()")
    public Object measureExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().getName();
        String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();

        System.out.println("TIMING: Calling method: " + className + "." + methodName);

        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis() - start;

        System.out.println("TIMING: Execution of " + className + "." + methodName + " finished. Time: " + end + " ms");
        return result;
    }
}
