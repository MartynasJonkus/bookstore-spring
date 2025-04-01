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
@ConditionalOnProperty(
        name = "features.logging.publisher-bean",
        havingValue = "true",
        matchIfMissing = true
)
public class PublisherBeanLoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Around("execution(* com.bookstore.bean.PublisherBean.*(..))")
    public Object logPublisherBeanMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        logger.info("PUBLISHER BEAN: Entering method '{}'", methodName);

        try {
            Object result = joinPoint.proceed(); // Execute the original method
            logger.info("PUBLISHER BEAN: Exiting method '{}'", methodName);
            return result;
        } catch (Exception e) {
            logger.error("PUBLISHER BEAN: Error in method '{}' - {}", methodName, e.getMessage());
            throw e;
        }
    }
}