package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.client.dto.UserDTO;
import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.infrastructure.dataobject.UserEntity;

import java.util.List;

/**
 * 用户转换器
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

    /**
     * Domain转DTO
     */
    public static UserDTO toDTO(User user, List<String> roles) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .status(user.getStatus())
                .roles(roles)
                .build();
    }
}
