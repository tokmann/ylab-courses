package ru.ylab.tasks.task5.timing.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Аспект для измерения времени выполнения методов в RestController.
 * Аспект перехватывает выполнение всех методов в классах, помеченных аннотацией @RestController,
 * измеряет время их выполнения и выводит результат в стандартный вывод.
 */
@Aspect
public class TimingAspect {

    /**
     * Срез точек соединения для методов в RestController.
     * Определяет, какие методы должны быть перехвачены аспектом - все методы в классах,
     * помеченных аннотацией @RestController.
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void anyRestControllerMethod() {}

    /**
     * Совет, выполняемый вокруг методов в RestController.
     * Измеряет время выполнения метода и выводит информацию в формате:
     * "LOGGING: <сигнатура метода> took <время> ms"
     * @param pjp точка соединения, предоставляющая информацию о методе
     * @return результат выполнения метода
     */
    @Around("anyRestControllerMethod()")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long time = System.currentTimeMillis() - start;

        System.out.println("LOGGING: " + pjp.getSignature() + " took " + time + " ms");
        return result;
    }
}

