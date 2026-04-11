package com.alibaba.cola.demo.app.service.impl;

import com.alibaba.cola.demo.app.convertor.RoleConvertor;
import com.alibaba.cola.demo.client.api.IRoleService;
import com.alibaba.cola.demo.client.dto.RoleAddCmd;
import com.alibaba.cola.demo.client.dto.UserRoleAssignCmd;
import com.alibaba.cola.demo.client.dto.data.RoleDTO;
import com.alibaba.cola.demo.domain.user.Role;
import com.alibaba.cola.demo.domain.user.gateway.RoleGateway;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 */
@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleGateway roleGateway;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response addRole(RoleAddCmd cmd) {
        Role role = Role.create(cmd.getRoleCode(), cmd.getRoleName());
        roleGateway.create(role);
        return Response.buildSuccess();
    }

    @Override
    @Transactional(readOnly = true)
    public MultiResponse<RoleDTO> listRoles() {
        List<Role> roles = roleGateway.listAll();
        List<RoleDTO> dtos = roles.stream()
                .map(RoleConvertor::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response assignRoleToUser(UserRoleAssignCmd cmd) {
        roleGateway.assignRoleToUser(cmd.getUserId(), cmd.getRoleId());
        return Response.buildSuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response removeRoleFromUser(Long userId, Long roleId) {
        roleGateway.removeRoleFromUser(userId, roleId);
        return Response.buildSuccess();
    }
}
