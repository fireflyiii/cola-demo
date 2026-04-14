package com.alibaba.cola.demo.adapter.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 统一请求日志切面
 * 记录每个 Controller 方法的请求路径、HTTP 方法、耗时
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequestLoggingAspect {

    @Around("execution(* com.alibaba.cola.demo.adapter.web..*Controller.*(..))")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.info("{} {} -> {}.{}() completed in {}ms", method, uri,
                    className.substring(className.lastIndexOf('.') + 1), methodName, duration);
            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.warn("{} {} -> {}.{}() failed in {}ms: {}", method, uri,
                    className.substring(className.lastIndexOf('.') + 1), methodName, duration,
                    ex.getMessage());
            throw ex;
        }
    }
}
