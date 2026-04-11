package com.alibaba.cola.demo.client.api;

import com.alibaba.cola.demo.client.dto.PermissionAddCmd;
import com.alibaba.cola.demo.client.dto.RoleAddCmd;
import com.alibaba.cola.demo.client.dto.RolePermissionAssignCmd;
import com.alibaba.cola.demo.client.dto.UserRoleAssignCmd;
import com.alibaba.cola.demo.client.dto.data.PermissionDTO;
import com.alibaba.cola.demo.client.dto.data.RoleDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;

/**
 * 角色服务接口
 */
public interface IRoleService {

    Response addRole(RoleAddCmd cmd);

    MultiResponse<RoleDTO> listRoles();

    Response assignRoleToUser(UserRoleAssignCmd cmd);

    Response removeRoleFromUser(Long userId, Long roleId);
}
