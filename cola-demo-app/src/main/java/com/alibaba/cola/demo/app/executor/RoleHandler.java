package com.alibaba.cola.demo.app.executor;

import com.alibaba.cola.demo.app.convertor.RoleConvertor;
import com.alibaba.cola.demo.client.dto.RoleAddCmd;
import com.alibaba.cola.demo.client.dto.UserRoleAssignCmd;
import com.alibaba.cola.demo.client.dto.data.RoleDTO;
import com.alibaba.cola.demo.domain.common.DomainEventPublisher;
import com.alibaba.cola.demo.domain.user.Role;
import com.alibaba.cola.demo.domain.user.RoleCreatedEvent;
import com.alibaba.cola.demo.domain.user.gateway.RoleGateway;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleHandler {

    private final RoleGateway roleGateway;
    private final RoleConvertor roleConvertor;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional(rollbackFor = Exception.class)
    public Response add(RoleAddCmd cmd) {
        Role role = Role.create(cmd.getRoleCode(), cmd.getRoleName());
        roleGateway.create(role);
        domainEventPublisher.publish(new RoleCreatedEvent(role.getRoleId(), role.getRoleCode()));
        return Response.buildSuccess();
    }

    @Transactional(readOnly = true)
    public MultiResponse<RoleDTO> list() {
        List<Role> roles = roleGateway.listAll();
        List<RoleDTO> dtos = roles.stream()
                .map(roleConvertor::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtos);
    }

    @Transactional(rollbackFor = Exception.class)
    public Response assignToUser(UserRoleAssignCmd cmd) {
        roleGateway.assignRoleToUser(cmd.getUserId(), cmd.getRoleId());
        return Response.buildSuccess();
    }

    @Transactional(rollbackFor = Exception.class)
    public Response removeFromUser(Long userId, Long roleId) {
        roleGateway.removeRoleFromUser(userId, roleId);
        return Response.buildSuccess();
    }
}
