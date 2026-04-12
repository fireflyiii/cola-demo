package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.apiapp.ApiApp;
import com.alibaba.cola.demo.infrastructure.dataobject.ApiAppEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * API应用装配器（Entity <-> Domain）
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApiAppAssembler {

    /**
     * Domain转Entity（自动生成，不映射id，由数据库生成）
     */
    ApiAppEntity toEntity(ApiApp apiApp);

    /**
     * Entity转Domain（使用rebuild方法，保留领域重建逻辑）
     */
    default ApiApp toDomain(ApiAppEntity entity) {
        if (entity == null) {
            return null;
        }
        return ApiApp.rebuild(entity.getId(), entity.getAppName(), entity.getApiKey(),
                entity.getAllowedPaths(), entity.getExpiresAt(), entity.getStatus());
    }
}
