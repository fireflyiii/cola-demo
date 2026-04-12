package com.alibaba.cola.demo.app.service.impl;

import com.alibaba.cola.demo.app.executor.PermissionHandler;
import com.alibaba.cola.demo.client.api.IPermissionService;
import com.alibaba.cola.demo.client.dto.PermissionAddCmd;
import com.alibaba.cola.demo.client.dto.RolePermissionAssignCmd;
import com.alibaba.cola.demo.client.dto.data.PermissionDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 权限服务实现
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements IPermissionService {

    private final PermissionHandler permissionHandler;

    @Override
    public Response addPermission(PermissionAddCmd cmd) {
        return permissionHandler.add(cmd);
    }

    @Override
    public MultiResponse<PermissionDTO> listPermissions() {
        return permissionHandler.list();
    }

    @Override
    public Response assignPermissionToRole(RolePermissionAssignCmd cmd) {
        return permissionHandler.assignToRole(cmd);
    }

    @Override
    public Response removePermissionFromRole(Long roleId, Long permissionId) {
        return permissionHandler.removeFromRole(roleId, permissionId);
    }
}
