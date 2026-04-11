package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.infrastructure.dataobject.UserEntity;

/**
 * 用户转换器（Entity <-> Domain）
 */
public class UserConvertor {

    private UserConvertor() {}

    /**
     * Entity转Domain
     */
    public static User toDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        User user = new User();
        user.setUserId(userEntity.getId());
        user.setUsername(userEntity.getUsername());
        user.setPassword(userEntity.getPassword());
        user.setStatus(userEntity.getStatus());
        return user;
    }
}
