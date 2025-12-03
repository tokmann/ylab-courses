package ru.ylab.tasks.task4.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Аспект для измерения времени выполнения методов контроллеров.
 * Замеряет и логирует время выполнения методов ProductController и UserController.
 */
@Aspect
@Component
public class TimingAspect {

    /**
     * Точка среза, определяющая все методы в ProductController и UserController.
     */
    @Pointcut("execution(* ru.ylab.tasks.task4.restcontroller.ProductRestController.*(..)) || "+
                     "execution(* ru.ylab.tasks.task4.restcontroller.UserRestController.*(..))")
    public void allControllerMethods() {
    }

    /**
     * Измеряет время выполнения методов контроллеров.
     * Логирует начало и окончание выполнения метода, а также затраченное время.
     * @param pjp точка соединения для продолжения выполнения метода
     * @return результат выполнения целевого метода
     */
    @Around("allControllerMethods()")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        String className = pjp.getTarget().getClass().getSimpleName();

        System.out.println("TIMING: Calling method: " + className + "." + methodName);

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long end = System.currentTimeMillis() - start;

        System.out.println("TIMING: Execution of " + className + "." + methodName + " finished. Time: " + end + " ms");
        return result;
    }
}
