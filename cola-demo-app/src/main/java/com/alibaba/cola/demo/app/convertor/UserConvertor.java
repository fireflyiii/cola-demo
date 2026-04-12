package com.alibaba.cola.demo.app.convertor;

import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.demo.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 用户DTO转换器
 */
@Mapper(componentModel = "spring")
public interface UserConvertor {

    @Mapping(target = "roles", source = "roles")
    UserDTO toDTO(User user, List<String> roles);
}
