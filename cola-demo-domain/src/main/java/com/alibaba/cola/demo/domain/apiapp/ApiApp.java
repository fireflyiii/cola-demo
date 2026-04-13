package com.alibaba.cola.demo.domain.apiapp;

import com.alibaba.cola.demo.client.common.BizErrorCode;
import com.alibaba.cola.demo.client.common.DomainException;
import com.alibaba.cola.demo.domain.common.AggregateRoot;
import com.alibaba.cola.demo.domain.common.PathMatcher;
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
        return PathMatcher.isPathAllowed(allowedPaths, requestPath);
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
}
