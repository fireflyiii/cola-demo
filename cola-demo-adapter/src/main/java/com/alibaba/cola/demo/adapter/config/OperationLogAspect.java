package com.alibaba.cola.demo.adapter.config;

import com.alibaba.cola.demo.client.common.OperationLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 操作审计日志切面
 * 拦截 @OperationLog 注解的方法，记录操作人、操作类型、请求路径和执行结果
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Around("@annotation(com.alibaba.cola.demo.client.common.OperationLog)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);

        String operator = getCurrentUser();
        String operationType = operationLog.type();
        String description = operationLog.description();
        String uri = getRequestUri();

        Object result;
        try {
            result = joinPoint.proceed();
            log.info("[Audit] operator={}, type={}, desc={}, uri={}, result=success",
                    operator, operationType, description, uri);
        } catch (Throwable ex) {
            log.warn("[Audit] operator={}, type={}, desc={}, uri={}, result=fail, error={}",
                    operator, operationType, description, uri, ex.getMessage());
            throw ex;
        }
        return result;
    }

    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }

    private String getRequestUri() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getRequestURI();
        }
        return "unknown";
    }
}
