package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.client.api.IRoleService;
import com.alibaba.cola.demo.client.dto.RoleAddCmd;
import com.alibaba.cola.demo.client.dto.RolePageQry;
import com.alibaba.cola.demo.client.dto.UserRoleAssignCmd;
import com.alibaba.cola.demo.client.dto.data.RoleDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 角色管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    /**
     * 添加角色
     */
    @PostMapping("/add")
    public Response addRole(@RequestBody @Valid RoleAddCmd cmd) {
        log.info("添加角色请求: roleCode={}", cmd.getRoleCode());
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
        log.info("分页查询角色请求: roleName={}, status={}, pageIndex={}, pageSize={}",
                qry.getRoleName(), qry.getStatus(), qry.getPageIndex(), qry.getPageSize());
        return roleService.pageRoles(qry);
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/assignToUser")
    public Response assignRoleToUser(@RequestBody @Valid UserRoleAssignCmd cmd) {
        log.info("分配角色请求: userId={}, roleId={}", cmd.getUserId(), cmd.getRoleId());
        return roleService.assignRoleToUser(cmd);
    }

    /**
     * 移除用户角色
     */
    @DeleteMapping("/removeFromUser")
    public Response removeRoleFromUser(@RequestParam Long userId, @RequestParam Long roleId) {
        log.info("移除角色请求: userId={}, roleId={}", userId, roleId);
        return roleService.removeRoleFromUser(userId, roleId);
    }
}
