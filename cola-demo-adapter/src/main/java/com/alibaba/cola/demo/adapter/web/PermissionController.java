package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.client.api.IPermissionService;
import com.alibaba.cola.demo.client.dto.PermissionAddCmd;
import com.alibaba.cola.demo.client.dto.RolePermissionAssignCmd;
import com.alibaba.cola.demo.client.dto.data.PermissionDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 权限管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private IPermissionService permissionService;

    /**
     * 添加权限
     */
    @PostMapping("/add")
    public Response addPermission(@RequestBody @Valid PermissionAddCmd cmd) {
        log.info("添加权限请求: permissionCode={}", cmd.getPermissionCode());
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
     * 为角色分配权限
     */
    @PostMapping("/assignToRole")
    public Response assignPermissionToRole(@RequestBody @Valid RolePermissionAssignCmd cmd) {
        log.info("分配权限请求: roleId={}, permissionId={}", cmd.getRoleId(), cmd.getPermissionId());
        return permissionService.assignPermissionToRole(cmd);
    }

    /**
     * 移除角色权限
     */
    @DeleteMapping("/removeFromRole")
    public Response removePermissionFromRole(@RequestParam Long roleId, @RequestParam Long permissionId) {
        log.info("移除权限请求: roleId={}, permissionId={}", roleId, permissionId);
        return permissionService.removePermissionFromRole(roleId, permissionId);
    }
}
