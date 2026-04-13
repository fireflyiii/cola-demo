package com.alibaba.cola.demo.domain.user.gateway;

import com.alibaba.cola.demo.client.dto.PermissionPageQry;
import com.alibaba.cola.demo.domain.user.Permission;
import com.alibaba.cola.dto.PageResponse;

import java.util.List;

/**
 * 权限网关接口
 */
public interface PermissionGateway {

    void create(Permission permission);

    List<Permission> listAll();

    PageResponse<Permission> page(PermissionPageQry qry);

    void assignPermissionToRole(Long roleId, Long permissionId);

    void removePermissionFromRole(Long roleId, Long permissionId);
}
