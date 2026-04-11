package com.alibaba.cola.demo.domain.user.gateway;

import com.alibaba.cola.demo.domain.user.Role;

import java.util.List;

/**
 * 角色网关接口
 */
public interface RoleGateway {

    void create(Role role);

    List<Role> listAll();

    void assignRoleToUser(Long userId, Long roleId);

    void removeRoleFromUser(Long userId, Long roleId);
}
