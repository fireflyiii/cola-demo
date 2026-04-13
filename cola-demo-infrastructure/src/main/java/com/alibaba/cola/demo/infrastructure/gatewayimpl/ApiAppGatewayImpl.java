package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.infrastructure.util.PageHelper;
import com.alibaba.cola.demo.client.dto.ApiAppPageQry;
import com.alibaba.cola.demo.domain.apiapp.ApiApp;
import com.alibaba.cola.demo.domain.apiapp.gateway.ApiAppGateway;
import com.alibaba.cola.demo.infrastructure.cache.CacheKeys;
import com.alibaba.cola.demo.infrastructure.cache.RedisCacheService;
import com.alibaba.cola.demo.infrastructure.convertor.ApiAppAssembler;
import com.alibaba.cola.demo.infrastructure.dataobject.ApiAppEntity;
import com.alibaba.cola.demo.infrastructure.mapper.ApiAppMapper;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
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
    private final RedisCacheService redisCacheService;

    @Override
    public void create(ApiApp apiApp) {
        ApiAppEntity entity = apiAppAssembler.toEntity(apiApp);
        apiAppMapper.insert(entity);
        apiApp.setId(entity.getId());
    }

    @Override
    public ApiApp findByApiKey(String apiKey) {
        String cacheKey = CacheKeys.API_APP_PREFIX + apiKey;
        return redisCacheService.getOrLoad(cacheKey, ApiApp.class, () -> {
            LambdaQueryWrapper<ApiAppEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ApiAppEntity::getApiKey, apiKey);
            return apiAppAssembler.toDomain(apiAppMapper.selectOne(wrapper));
        }, Duration.ofSeconds(CacheKeys.API_APP_TTL_SECONDS));
    }

    @Override
    public List<ApiApp> findAll() {
        return apiAppMapper.selectList(null).stream()
                .map(apiAppAssembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<ApiApp> page(ApiAppPageQry qry) {
        LambdaQueryWrapper<ApiAppEntity> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(qry.getAppName())) {
            wrapper.like(ApiAppEntity::getAppName, qry.getAppName());
        }
        if (qry.getStatus() != null) {
            wrapper.eq(ApiAppEntity::getStatus, qry.getStatus());
        }
        return PageHelper.toPageResponse(
                apiAppMapper.selectPage(PageHelper.toPage(qry), wrapper),
                apiAppAssembler::toDomain
        );
    }
}
