package com.alibaba.cola.demo.app.convertor;

import com.alibaba.cola.demo.client.dto.data.RoleDTO;
import com.alibaba.cola.demo.domain.user.Role;
import org.mapstruct.Mapper;

/**
 * 角色DTO转换器
 */
@Mapper(componentModel = "spring")
public interface RoleConvertor {

    /**
     * Domain -> DTO
     */
    RoleDTO toDTO(Role role);
}
