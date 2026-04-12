package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.user.Password;
import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.infrastructure.dataobject.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 用户装配器（Entity <-> Domain）
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserAssembler {

    /**
     * Domain转Entity（自动生成，不映射id，由数据库生成）
     */
    UserEntity toEntity(User user);

    /**
     * Entity转Domain（使用工厂方法，保留领域重建逻辑）
     */
    default User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.rebuild(entity.getId(), entity.getUsername(), entity.getPassword(), entity.getStatus());
    }

    /**
     * Password值对象 → String（MapStruct自动识别此方法用于类型转换）
     */
    default String mapPassword(Password password) {
        return password != null ? password.getEncoded() : null;
    }
}
