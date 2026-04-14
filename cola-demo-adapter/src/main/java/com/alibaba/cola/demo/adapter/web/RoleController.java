package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.client.api.IRoleService;
import com.alibaba.cola.demo.client.common.OperationLog;
import com.alibaba.cola.demo.client.dto.RoleAddCmd;
import com.alibaba.cola.demo.client.dto.RolePageQry;
import com.alibaba.cola.demo.client.dto.UserRoleAssignCmd;
import com.alibaba.cola.demo.client.dto.data.RoleDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    /**
     * 添加角色
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(type = "新增", description = "添加角色")
    public Response addRole(@RequestBody @Valid RoleAddCmd cmd) {
        return roleService.addRole(cmd);
    }

    /**
     * 查询所有角色
     */
    @GetMapping("/list")
    public MultiResponse<RoleDTO> listRoles() {
        return roleService.listRoles();
    }

    /**
     * 分页查询角色
     */
    @PostMapping("/page")
    public PageResponse<RoleDTO> pageRoles(@RequestBody RolePageQry qry) {
        return roleService.pageRoles(qry);
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/assignToUser")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(type = "分配", description = "为用户分配角色")
    public Response assignRoleToUser(@RequestBody @Valid UserRoleAssignCmd cmd) {
        return roleService.assignRoleToUser(cmd);
    }

    /**
     * 移除用户角色
     */
    @DeleteMapping("/removeFromUser")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(type = "移除", description = "移除用户角色")
    public Response removeRoleFromUser(@RequestParam Long userId, @RequestParam Long roleId) {
        return roleService.removeRoleFromUser(userId, roleId);
    }
}
