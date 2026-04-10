package com.alibaba.cola.demo.client.api;

import com.alibaba.cola.demo.client.dto.UserDTO;

/**
 * 用户服务接口
 */
public interface IUserService {

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户DTO
     */
    UserDTO getUserByUsername(String username);
}
