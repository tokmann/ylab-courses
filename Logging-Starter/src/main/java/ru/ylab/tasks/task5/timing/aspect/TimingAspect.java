package ru.ylab.tasks.task5.timing.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class TimingAspect {

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void anyRestControllerMethod() {}

    @Around("anyRestControllerMethod()")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long time = System.currentTimeMillis() - start;

        System.out.println("LOGGING: " + pjp.getSignature() + " took " + time + " ms");
        return result;
    }
}

