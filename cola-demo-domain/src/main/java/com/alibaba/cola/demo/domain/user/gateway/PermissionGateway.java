package com.alibaba.cola.demo.domain.user.gateway;

import com.alibaba.cola.demo.domain.user.Permission;

import java.util.List;

/**
 * 权限网关接口
 */
public interface PermissionGateway {

    void create(Permission permission);

    List<Permission> listAll();

    void assignPermissionToRole(Long roleId, Long permissionId);

    void removePermissionFromRole(Long roleId, Long permissionId);
}
