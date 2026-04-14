package com.alibaba.cola.demo.client.api;

import com.alibaba.cola.demo.client.dto.data.UserAuthInfoDTO;
import com.alibaba.cola.demo.client.dto.data.UserDTO;

/**
 * 认证服务接口
 * 仅包含用户信息查询，Token生成/刷新由Adapter层AuthHandler直接处理
 */
public interface IAuthService {

    /**
     * 根据用户名获取用户信息
     */
    UserDTO getUserInfo(String username);

    /**
     * 获取用户认证信息（供Adapter层UserDetailsService使用）
     */
    UserAuthInfoDTO loadUserAuthInfo(String username);
}
