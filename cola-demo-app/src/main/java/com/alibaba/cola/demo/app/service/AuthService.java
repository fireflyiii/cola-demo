package com.alibaba.cola.demo.app.service;

import com.alibaba.cola.demo.app.convertor.UserConvertor;
import com.alibaba.cola.demo.client.dto.LoginCmd;
import com.alibaba.cola.demo.client.dto.LoginResponse;
import com.alibaba.cola.demo.client.dto.data.UserAuthInfo;
import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserGateway userGateway;
    private final UserConvertor userConvertor;

    /**
     * 用户登录
     */
    public LoginResponse login(LoginCmd loginCmd, String token, Long expiresIn) {
        String username = loginCmd.getUsername();
        User user = userGateway.findByUsername(username);
        List<String> roles = userGateway.findRoleCodesByUsername(username);

        return new LoginResponse(token, expiresIn, "Bearer", userConvertor.toDTO(user, roles));
    }

    /**
     * 根据用户名获取用户信息
     */
    public UserDTO getUserInfo(String username) {
        User user = userGateway.findByUsername(username);
        List<String> roles = userGateway.findRoleCodesByUsername(username);
        return userConvertor.toDTO(user, roles);
    }

    /**
     * 获取用户认证信息（供Adapter层UserDetailsService使用）
     */
    public UserAuthInfo loadUserAuthInfo(String username) {
        User user = userGateway.findByUsername(username);
        if (user == null) {
            return null;
        }
        List<String> roleCodes = userGateway.findRoleCodesByUsername(username);
        List<String> permissionCodes = userGateway.findPermissionsByRoleCodes(roleCodes);

        return new UserAuthInfo(
                user.getUsername(),
                user.getPassword().getEncoded(),
                user.isEnabled(),
                roleCodes,
                permissionCodes
        );
    }
}
