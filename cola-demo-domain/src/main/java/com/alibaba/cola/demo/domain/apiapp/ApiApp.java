package com.alibaba.cola.demo.domain.apiapp;

import com.alibaba.cola.demo.client.common.BizErrorCode;
import com.alibaba.cola.demo.client.common.DomainException;
import com.alibaba.cola.demo.domain.common.AggregateRoot;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * API应用聚合根
 */
@Getter
public class ApiApp implements AggregateRoot {

    private Long id;
    private String appName;
    private String apiKey;
    private String allowedPaths;
    private LocalDateTime expiresAt;
    private Integer status;

    /**
     * 创建API应用
     */
    public static ApiApp create(String appName, String allowedPaths) {
        if (appName == null || appName.trim().isEmpty()) {
            throw new DomainException(BizErrorCode.B_API_APP_NAME_NOT_BLANK);
        }
        ApiApp app = new ApiApp();
        app.appName = appName;
        app.apiKey = generateApiKey();
        app.allowedPaths = allowedPaths;
        app.expiresAt = null; // 永不过期
        app.status = 1; // 启用
        return app;
    }

    /**
     * 重建API应用（由Assembler从持久化层加载时使用）
     */
    public static ApiApp rebuild(Long id, String appName, String apiKey, String allowedPaths,
                                  LocalDateTime expiresAt, Integer status) {
        ApiApp app = new ApiApp();
        app.id = id;
        app.appName = appName;
        app.apiKey = apiKey;
        app.allowedPaths = allowedPaths;
        app.expiresAt = expiresAt;
        app.status = status;
        return app;
    }

    /**
     * 判断是否启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }

    /**
     * 判断是否已过期
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    /**
     * 判断是否允许访问指定路径
     */
    public boolean isPathAllowed(String requestPath) {
        if (allowedPaths == null || allowedPaths.trim().isEmpty()) {
            return false;
        }
        String[] patterns = allowedPaths.split(",");
        for (String pattern : patterns) {
            if (matchPath(pattern.trim(), requestPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置ID（由Gateway在持久化后回填主键）
     */
    public void setId(Long id) {
        this.id = id;
    }

    private static String generateApiKey() {
        return "ak_" + UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 简单的Ant路径匹配（支持 * 和 **）
     */
    private boolean matchPath(String pattern, String path) {
        // 精确匹配
        if (!pattern.contains("*")) {
            return pattern.equals(path);
        }
        // /** 匹配所有子路径
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }
        // /* 匹配单层路径
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            if (!path.startsWith(prefix)) {
                return false;
            }
            String remaining = path.substring(prefix.length());
            return !remaining.contains("/");
        }
        return false;
    }
}
