package com.alibaba.cola.demo.app.convertor;

import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.demo.domain.user.User;

import java.util.List;

/**
 * 用户DTO转换器
 */
public class UserConvertor {

    private UserConvertor() {}

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
