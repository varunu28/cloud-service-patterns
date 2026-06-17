package io.varunu28.bulkheadlite;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Component
@Aspect
public class BulkheadAspect {

    private final Map<String, Semaphore> semaphores = new ConcurrentHashMap<>();

    @Around("@annotation(bulkhead)")
    public Object apply(ProceedingJoinPoint joinPoint, CustomBulkhead annotation) throws Throwable {
        Semaphore semaphore = semaphores.computeIfAbsent(
                annotation.name(),
                _ -> new Semaphore(annotation.maxConcurrent(), true)
        );
        if (semaphore.tryAcquire()) {
            try {
                return joinPoint.proceed();
            } finally {
                semaphore.release();
            }
        } else {
            throw new BulkheadFullException("Bulkhead: " + annotation.name() + " is full. Request rejected");
        }
    }
}
