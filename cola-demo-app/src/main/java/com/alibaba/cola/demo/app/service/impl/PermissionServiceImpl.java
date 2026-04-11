package com.alibaba.cola.demo.app.service.impl;

import com.alibaba.cola.demo.app.convertor.PermissionConvertor;
import com.alibaba.cola.demo.client.api.IPermissionService;
import com.alibaba.cola.demo.client.dto.PermissionAddCmd;
import com.alibaba.cola.demo.client.dto.RolePermissionAssignCmd;
import com.alibaba.cola.demo.client.dto.data.PermissionDTO;
import com.alibaba.cola.demo.domain.user.Permission;
import com.alibaba.cola.demo.domain.user.gateway.PermissionGateway;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现
 */
@Service
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    private PermissionGateway permissionGateway;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response addPermission(PermissionAddCmd cmd) {
        Permission permission = Permission.create(
                cmd.getPermissionCode(),
                cmd.getPermissionName(),
                cmd.getResourceType(),
                cmd.getResourcePath()
        );
        permissionGateway.create(permission);
        return Response.buildSuccess();
    }

    @Override
    @Transactional(readOnly = true)
    public MultiResponse<PermissionDTO> listPermissions() {
        List<Permission> permissions = permissionGateway.listAll();
        List<PermissionDTO> dtos = permissions.stream()
                .map(PermissionConvertor::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response assignPermissionToRole(RolePermissionAssignCmd cmd) {
        permissionGateway.assignPermissionToRole(cmd.getRoleId(), cmd.getPermissionId());
        return Response.buildSuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response removePermissionFromRole(Long roleId, Long permissionId) {
        permissionGateway.removePermissionFromRole(roleId, permissionId);
        return Response.buildSuccess();
    }
}
