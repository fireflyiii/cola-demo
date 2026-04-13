package com.alibaba.cola.demo.app.service.impl;

import com.alibaba.cola.demo.app.executor.RoleHandler;
import com.alibaba.cola.demo.client.api.IRoleService;
import com.alibaba.cola.demo.client.dto.RoleAddCmd;
import com.alibaba.cola.demo.client.dto.RolePageQry;
import com.alibaba.cola.demo.client.dto.UserRoleAssignCmd;
import com.alibaba.cola.demo.client.dto.data.RoleDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 角色服务实现
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final RoleHandler roleHandler;

    @Override
    public Response addRole(RoleAddCmd cmd) {
        return roleHandler.add(cmd);
    }

    @Override
    public MultiResponse<RoleDTO> listRoles() {
        return roleHandler.list();
    }

    @Override
    public Response assignRoleToUser(UserRoleAssignCmd cmd) {
        return roleHandler.assignToUser(cmd);
    }

    @Override
    public Response removeRoleFromUser(Long userId, Long roleId) {
        return roleHandler.removeFromUser(userId, roleId);
    }

    @Override
    public PageResponse<RoleDTO> pageRoles(RolePageQry qry) {
        return roleHandler.page(qry);
    }
}
