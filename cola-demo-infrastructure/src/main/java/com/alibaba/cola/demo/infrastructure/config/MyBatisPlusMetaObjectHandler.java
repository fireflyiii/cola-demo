package com.alibaba.cola.demo.infrastructure.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 自动填充创建人、创建时间、更新人、更新时间等通用字段
 */
@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisPlusMetaObjectHandler.class);

    private static final String CREATED_BY = "createdBy";
    private static final String CREATED_TIME = "createdTime";
    private static final String UPDATED_BY = "updatedBy";
    private static final String UPDATED_TIME = "updatedTime";

    @Override
    public void insertFill(MetaObject metaObject) {
        LOGGER.debug("Start insert fill for metaObject: {}", metaObject.getOriginalObject().getClass().getSimpleName());

        // 填充创建人
        strictInsertFill(metaObject, CREATED_BY, String.class, getCurrentUsername());
        // 填充创建时间
        strictInsertFill(metaObject, CREATED_TIME, LocalDateTime.class, LocalDateTime.now());
        // 填充更新人
        strictInsertFill(metaObject, UPDATED_BY, String.class, getCurrentUsername());
        // 填充更新时间
        strictInsertFill(metaObject, UPDATED_TIME, LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LOGGER.debug("Start update fill for metaObject: {}", metaObject.getOriginalObject().getClass().getSimpleName());

        // 填充更新人
        strictUpdateFill(metaObject, UPDATED_BY, String.class, getCurrentUsername());
        // 填充更新时间
        strictUpdateFill(metaObject, UPDATED_TIME, LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 获取当前登录用户名
     * 注意：如果需要从SecurityContext获取用户名，adapter层注入UserDetailsService获取
     */
    protected String getCurrentUsername() {
        // 简单实现，未登录时使用system
        // 实际项目中可以通过ThreadLocal或其他方式传递当前用户
        return "system";
    }
}
