package com.alibaba.cola.demo.app.convertor;

import com.alibaba.cola.demo.client.dto.data.PermissionDTO;
import com.alibaba.cola.demo.domain.user.Permission;
import org.mapstruct.Mapper;

/**
 * 权限DTO转换器
 */
@Mapper(componentModel = "spring")
public interface PermissionConvertor {

    /**
     * Domain -> DTO
     */
    PermissionDTO toDTO(Permission permission);
}
