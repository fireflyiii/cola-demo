package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.client.api.IPermissionService;
import com.alibaba.cola.demo.client.common.OperationLog;
import com.alibaba.cola.demo.client.dto.PermissionAddCmd;
import com.alibaba.cola.demo.client.dto.PermissionPageQry;
import com.alibaba.cola.demo.client.dto.RolePermissionAssignCmd;
import com.alibaba.cola.demo.client.dto.data.PermissionDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final IPermissionService permissionService;

    /**
     * 添加权限
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(type = "新增", description = "添加权限")
    public Response addPermission(@RequestBody @Valid PermissionAddCmd cmd) {
        return permissionService.addPermission(cmd);
    }

    /**
     * 查询所有权限
     */
    @GetMapping("/list")
    public MultiResponse<PermissionDTO> listPermissions() {
        return permissionService.listPermissions();
    }

    /**
     * 分页查询权限
     */
    @PostMapping("/page")
    public PageResponse<PermissionDTO> pagePermissions(@RequestBody PermissionPageQry qry) {
        return permissionService.pagePermissions(qry);
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/assignToRole")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(type = "分配", description = "为角色分配权限")
    public Response assignPermissionToRole(@RequestBody @Valid RolePermissionAssignCmd cmd) {
        return permissionService.assignPermissionToRole(cmd);
    }

    /**
     * 移除角色权限
     */
    @DeleteMapping("/removeFromRole")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(type = "移除", description = "移除角色权限")
    public Response removePermissionFromRole(@RequestParam Long roleId, @RequestParam Long permissionId) {
        return permissionService.removePermissionFromRole(roleId, permissionId);
    }
}
