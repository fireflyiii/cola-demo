package com.alibaba.cola.demo.infrastructure.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 自动填充创建人、创建时间、更新人、更新时间等通用字段
 */
@Slf4j
@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    private static final String CREATED_BY = "createdBy";
    private static final String CREATED_TIME = "createdTime";
    private static final String UPDATED_BY = "updatedBy";
    private static final String UPDATED_TIME = "updatedTime";

    @Override
    public void insertFill(MetaObject metaObject) {
        String currentUser = getCurrentUsername();
        strictInsertFill(metaObject, CREATED_BY, String.class, currentUser);
        strictInsertFill(metaObject, CREATED_TIME, LocalDateTime.class, LocalDateTime.now());
        strictInsertFill(metaObject, UPDATED_BY, String.class, currentUser);
        strictInsertFill(metaObject, UPDATED_TIME, LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String currentUser = getCurrentUsername();
        strictUpdateFill(metaObject, UPDATED_BY, String.class, currentUser);
        strictUpdateFill(metaObject, UPDATED_TIME, LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 获取当前登录用户名
     * 从SecurityContext获取，未登录时使用"system"
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("Failed to get current username from SecurityContext", e);
        }
        return "system";
    }
}
