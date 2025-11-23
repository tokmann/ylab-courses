package ru.ylab.tasks.task3.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclarePrecedence;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class TimingAspect {

    @Pointcut("execution(* ru.ylab.tasks.task3.controller.ProductController.*(..)) || "+
                     "execution(* ru.ylab.tasks.task3.controller.UserController.*(..))")
    public void allControllerMethods() {
    }

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
