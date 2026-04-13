package com.alibaba.cola.demo.client.api;

import com.alibaba.cola.demo.client.dto.PermissionAddCmd;
import com.alibaba.cola.demo.client.dto.PermissionPageQry;
import com.alibaba.cola.demo.client.dto.RolePermissionAssignCmd;
import com.alibaba.cola.demo.client.dto.data.PermissionDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;

/**
 * 权限服务接口
 */
public interface IPermissionService {

    Response addPermission(PermissionAddCmd cmd);

    MultiResponse<PermissionDTO> listPermissions();

    PageResponse<PermissionDTO> pagePermissions(PermissionPageQry qry);

    Response assignPermissionToRole(RolePermissionAssignCmd cmd);

    Response removePermissionFromRole(Long roleId, Long permissionId);
}
