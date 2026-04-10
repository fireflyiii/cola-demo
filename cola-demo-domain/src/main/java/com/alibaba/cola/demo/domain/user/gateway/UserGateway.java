package com.alibaba.cola.demo.domain.user.gateway;

import com.alibaba.cola.demo.domain.user.User;

import java.util.List;

/**
 * 用户网关接口
 */
public interface UserGateway {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    User findByUsername(String username);

    /**
     * 根据用户名查询角色编码列表
     * @param username 用户名
     * @return 角色编码列表
     */
    List<String> findRoleCodesByUsername(String username);

    /**
     * 根据角色编码列表查询权限编码列表
     * @param roleCodes 角色编码列表
     * @return 权限编码列表
     */
    List<String> findPermissionsByRoleCodes(List<String> roleCodes);

    /**
     * 更新用户密码
     * @param username 用户名
     * @param encodedPassword 加密后的密码
     * @return 是否更新成功
     */
    boolean updatePassword(String username, String encodedPassword);
}
