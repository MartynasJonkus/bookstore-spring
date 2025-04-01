package com.bookstore.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(name = "features.tracking.performance", havingValue = "true")
public class PerformanceTrackingAspect {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Around("@annotation(com.bookstore.aspect.TrackPerformance)")
    public Object trackExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - startTime;

        logger.info("{} executed in {} ms",
                joinPoint.getSignature().toShortString(),
                executionTime
        );

        return result;
    }
}