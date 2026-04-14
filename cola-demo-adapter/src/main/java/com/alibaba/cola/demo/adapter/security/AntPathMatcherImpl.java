package com.alibaba.cola.demo.adapter.security;

import com.alibaba.cola.demo.domain.common.PathMatcher;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 基于 Spring AntPathMatcher 的路径匹配实现
 * 处理 URL 编码绕过等安全问题
 */
@Component
public class AntPathMatcherImpl implements PathMatcher {

    private final org.springframework.util.AntPathMatcher delegate = new org.springframework.util.AntPathMatcher();

    @Override
    public boolean isPathAllowed(String allowedPaths, String requestPath) {
        if (allowedPaths == null || allowedPaths.trim().isEmpty()) {
            return false;
        }
        // 解码 URL，防止编码绕过（如 %2F 代替 /）
        String decodedPath = decodeUrl(requestPath);
        for (String pattern : allowedPaths.split(",")) {
            String trimmedPattern = pattern.trim();
            if (delegate.match(trimmedPattern, decodedPath)) {
                return true;
            }
        }
        return false;
    }

    private String decodeUrl(String path) {
        try {
            return URLDecoder.decode(path, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return path;
        }
    }
}
