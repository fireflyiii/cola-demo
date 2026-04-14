package com.alibaba.cola.demo.app.executor;

import com.alibaba.cola.demo.app.convertor.PermissionConvertor;
import com.alibaba.cola.demo.client.common.PageResult;
import com.alibaba.cola.demo.client.dto.PermissionAddCmd;
import com.alibaba.cola.demo.client.dto.PermissionPageQry;
import com.alibaba.cola.demo.client.dto.RolePermissionAssignCmd;
import com.alibaba.cola.demo.client.dto.data.PermissionDTO;
import com.alibaba.cola.demo.domain.user.Permission;
import com.alibaba.cola.demo.domain.user.gateway.PermissionGateway;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionHandler {

    private final PermissionGateway permissionGateway;
    private final PermissionConvertor permissionConvertor;

    @Transactional(rollbackFor = Exception.class)
    public Response add(PermissionAddCmd cmd) {
        Permission permission = Permission.create(
                cmd.getPermissionCode(), cmd.getPermissionName(),
                cmd.getResourceType(), cmd.getResourcePath());
        permissionGateway.create(permission);
        return Response.buildSuccess();
    }

    @Transactional(readOnly = true)
    public MultiResponse<PermissionDTO> list() {
        List<Permission> permissions = permissionGateway.listAll();
        List<PermissionDTO> dtos = permissions.stream()
                .map(permissionConvertor::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtos);
    }

    @Transactional(rollbackFor = Exception.class)
    public Response assignToRole(RolePermissionAssignCmd cmd) {
        permissionGateway.assignPermissionToRole(cmd.getRoleId(), cmd.getPermissionId());
        return Response.buildSuccess();
    }

    @Transactional(rollbackFor = Exception.class)
    public Response removeFromRole(Long roleId, Long permissionId) {
        permissionGateway.removePermissionFromRole(roleId, permissionId);
        return Response.buildSuccess();
    }

    @Transactional(readOnly = true)
    public PageResponse<PermissionDTO> page(PermissionPageQry qry) {
        PageResult.validatePageSize(qry.getPageSize());
        return PageResult.map(permissionGateway.page(qry), permissionConvertor::toDTO);
    }
}
