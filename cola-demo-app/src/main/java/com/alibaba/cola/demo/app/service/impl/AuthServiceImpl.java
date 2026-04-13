package com.alibaba.cola.demo.app.service.impl;

import com.alibaba.cola.demo.app.convertor.UserConvertor;
import com.alibaba.cola.demo.client.api.IAuthService;
import com.alibaba.cola.demo.client.dto.LoginCmd;
import com.alibaba.cola.demo.client.dto.LoginResponse;
import com.alibaba.cola.demo.client.dto.data.UserAuthInfoDTO;
import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserGateway userGateway;
    private final UserConvertor userConvertor;

    @Override
    public LoginResponse login(LoginCmd loginCmd, String token, String refreshToken, Long expiresIn) {
        String username = loginCmd.getUsername();
        User user = userGateway.findByUsername(username);
        List<String> roles = userGateway.findRoleCodesByUsername(username);

        return new LoginResponse(token, expiresIn, "Bearer", refreshToken, userConvertor.toDTO(user, roles));
    }

    @Override
    public LoginResponse refreshToken(String username, String refreshToken) {
        User user = userGateway.findByUsername(username);
        if (user == null) {
            return null;
        }
        List<String> roles = userGateway.findRoleCodesByUsername(username);
        // 实际的新Token生成由Adapter层完成，此处仅返回用户信息
        // refreshToken由Controller层重新签发
        return new LoginResponse(null, null, "Bearer", refreshToken, userConvertor.toDTO(user, roles));
    }

    @Override
    public UserDTO getUserInfo(String username) {
        User user = userGateway.findByUsername(username);
        List<String> roles = userGateway.findRoleCodesByUsername(username);
        return userConvertor.toDTO(user, roles);
    }

    @Override
    public UserAuthInfoDTO loadUserAuthInfo(String username) {
        User user = userGateway.findByUsername(username);
        if (user == null) {
            return null;
        }
        List<String> roleCodes = userGateway.findRoleCodesByUsername(username);
        List<String> permissionCodes = userGateway.findPermissionsByRoleCodes(roleCodes);

        return new UserAuthInfoDTO(
                user.getUsername(),
                user.getPassword().getEncoded(),
                user.isEnabled(),
                roleCodes,
                permissionCodes
        );
    }
}
