package com.alibaba.cola.demo.app.service;

import com.alibaba.cola.demo.client.dto.LoginCmd;
import com.alibaba.cola.demo.client.dto.LoginResponse;
import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import com.alibaba.cola.demo.app.convertor.UserConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private UserGateway userGateway;

    /**
     * 用户登录
     */
    public LoginResponse login(LoginCmd loginCmd, String token, Long expiresIn) {
        String username = loginCmd.getUsername();
        User user = userGateway.findByUsername(username);
        List<String> roles = userGateway.findRoleCodesByUsername(username);

        return new LoginResponse(token, expiresIn, "Bearer", UserConvertor.toDTO(user, roles));
    }

    /**
     * 根据用户名获取用户信息
     */
    public UserDTO getUserInfo(String username) {
        User user = userGateway.findByUsername(username);
        List<String> roles = userGateway.findRoleCodesByUsername(username);
        return UserConvertor.toDTO(user, roles);
    }
}
