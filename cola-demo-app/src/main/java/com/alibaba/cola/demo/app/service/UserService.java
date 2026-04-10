package com.alibaba.cola.demo.app.service;

import com.alibaba.cola.demo.client.api.IUserService;
import com.alibaba.cola.demo.client.dto.UserDTO;
import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现
 */
@Service
public class UserService implements IUserService {

    private final UserGateway userGateway;

    public UserService(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userGateway.findByUsername(username);
        List<String> roles = userGateway.findRoleCodesByUsername(username);
        return convertToDTO(user, roles);
    }

    private UserDTO convertToDTO(User user, List<String> roles) {
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
