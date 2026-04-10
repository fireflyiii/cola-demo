package com.alibaba.cola.demo.app.service;

import com.alibaba.cola.demo.client.dto.LoginCmd;
import com.alibaba.cola.demo.client.dto.LoginResponse;
import com.alibaba.cola.demo.client.dto.UserDTO;
import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务
 */
@Service
public class AuthService {

    private final UserGateway userGateway;

    public AuthService(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    /**
     * 用户登录
     * @param loginCmd 登录命令
     * @param token JWT Token
     * @param expiresIn 过期时间（秒）
     * @return 登录响应
     */
    public LoginResponse login(LoginCmd loginCmd, String token, Long expiresIn) {
        String username = loginCmd.getUsername();
        User user = userGateway.findByUsername(username);
        List<String> roles = userGateway.findRoleCodesByUsername(username);
        UserDTO userDTO = convertToDTO(user, roles);

        return LoginResponse.builder()
                .token(token)
                .expiresIn(expiresIn)
                .tokenType("Bearer")
                .user(userDTO)
                .build();
    }

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户DTO
     */
    public UserDTO getUserInfo(String username) {
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
