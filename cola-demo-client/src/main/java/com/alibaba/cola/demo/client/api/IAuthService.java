package com.alibaba.cola.demo.client.api;

import com.alibaba.cola.demo.client.dto.LoginCmd;
import com.alibaba.cola.demo.client.dto.LoginResponse;
import com.alibaba.cola.demo.client.dto.data.UserAuthInfoDTO;
import com.alibaba.cola.demo.client.dto.data.UserDTO;

/**
 * 认证服务接口
 */
public interface IAuthService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginCmd loginCmd, String token, String refreshToken, Long expiresIn);

    /**
     * 刷新Token
     */
    LoginResponse refreshToken(String username, String accessToken, String refreshToken, Long expiresIn);

    /**
     * 根据用户名获取用户信息
     */
    UserDTO getUserInfo(String username);

    /**
     * 获取用户认证信息（供Adapter层UserDetailsService使用）
     */
    UserAuthInfoDTO loadUserAuthInfo(String username);
}
