package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.domain.apiapp.ApiApp;
import com.alibaba.cola.demo.domain.apiapp.gateway.ApiAppGateway;
import com.alibaba.cola.demo.infrastructure.convertor.ApiAppAssembler;
import com.alibaba.cola.demo.infrastructure.dataobject.ApiAppEntity;
import com.alibaba.cola.demo.infrastructure.mapper.ApiAppMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API应用网关实现
 */
@Component
@RequiredArgsConstructor
public class ApiAppGatewayImpl implements ApiAppGateway {

    private final ApiAppMapper apiAppMapper;
    private final ApiAppAssembler apiAppAssembler;

    @Override
    public void create(ApiApp apiApp) {
        ApiAppEntity entity = apiAppAssembler.toEntity(apiApp);
        apiAppMapper.insert(entity);
        apiApp.setId(entity.getId());
    }

    @Override
    public ApiApp findByApiKey(String apiKey) {
        LambdaQueryWrapper<ApiAppEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiAppEntity::getApiKey, apiKey);
        return apiAppAssembler.toDomain(apiAppMapper.selectOne(wrapper));
    }

    @Override
    public List<ApiApp> findAll() {
        return apiAppMapper.selectList(null).stream()
                .map(apiAppAssembler::toDomain)
                .collect(Collectors.toList());
    }
}
