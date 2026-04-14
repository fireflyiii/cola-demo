package com.alibaba.cola.demo.app.service.impl;

import com.alibaba.cola.demo.app.convertor.UserConvertor;
import com.alibaba.cola.demo.client.api.IAuthService;
import com.alibaba.cola.demo.client.common.BizErrorCode;
import com.alibaba.cola.demo.client.common.DomainException;
import com.alibaba.cola.demo.client.dto.data.UserAuthInfoDTO;
import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务实现（领域服务）
 * 仅处理用户信息查询，Token生成等适配器关注点由 AuthHandler 处理
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserGateway userGateway;
    private final UserConvertor userConvertor;

    @Override
    public UserDTO getUserInfo(String username) {
        User user = userGateway.findByUsername(username);
        if (user == null) {
            throw new DomainException(BizErrorCode.B_USER_NOT_FOUND);
        }
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
