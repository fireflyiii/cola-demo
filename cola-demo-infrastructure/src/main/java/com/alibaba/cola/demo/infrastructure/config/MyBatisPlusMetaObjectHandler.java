package com.alibaba.cola.demo.infrastructure.config;

import com.alibaba.cola.demo.domain.common.CurrentUserProvider;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 自动填充创建人、创建时间、更新人、更新时间等通用字段
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    private final CurrentUserProvider currentUserProvider;

    private static final String CREATED_BY = "createdBy";
    private static final String CREATED_TIME = "createdTime";
    private static final String UPDATED_BY = "updatedBy";
    private static final String UPDATED_TIME = "updatedTime";

    @Override
    public void insertFill(MetaObject metaObject) {
        String currentUser = currentUserProvider.getCurrentUsername();
        strictInsertFill(metaObject, CREATED_BY, String.class, currentUser);
        strictInsertFill(metaObject, CREATED_TIME, LocalDateTime.class, LocalDateTime.now());
        strictInsertFill(metaObject, UPDATED_BY, String.class, currentUser);
        strictInsertFill(metaObject, UPDATED_TIME, LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String currentUser = currentUserProvider.getCurrentUsername();
        strictUpdateFill(metaObject, UPDATED_BY, String.class, currentUser);
        strictUpdateFill(metaObject, UPDATED_TIME, LocalDateTime.class, LocalDateTime.now());
    }
}
