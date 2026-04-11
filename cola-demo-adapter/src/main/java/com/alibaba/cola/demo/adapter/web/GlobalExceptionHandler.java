package com.alibaba.cola.demo.adapter.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 * 统一处理认证相关的异常，返回友好的错误信息
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理认证失败（用户名或密码错误）
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
    }

    /**
     * 处理用户不存在
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UsernameNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "用户不存在");
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "认证失败: " + ex.getMessage());
    }

    /**
     * 处理Token过期或无效
     */
    @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleTokenExpired(Exception ex) {
        log.warn("Token expired: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Token已过期，请重新登录");
    }

    /**
     * 通用异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 处理参数校验失败（@Valid）
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", message);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理约束违反（@Validated）
     */
    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(javax.validation.ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        log.warn("Constraint violation: {}", message);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理请求体格式错误（JSON解析失败）
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        log.warn("Request body not readable: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "请求体格式错误");
    }

    /**
     * 处理访问拒绝（权限不足）
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "权限不足");
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("errCode", status.value());
        body.put("errMessage", message);
        return ResponseEntity.status(status).body(body);
    }
}
