package com.alibaba.cola.demo.app.executor;

import com.alibaba.cola.demo.app.convertor.ApiAppConvertor;
import com.alibaba.cola.demo.client.common.PageResult;
import com.alibaba.cola.demo.client.dto.ApiAppAddCmd;
import com.alibaba.cola.demo.client.dto.ApiAppPageQry;
import com.alibaba.cola.demo.client.dto.data.ApiAppDTO;
import com.alibaba.cola.demo.domain.apiapp.ApiApp;
import com.alibaba.cola.demo.domain.apiapp.gateway.ApiAppGateway;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApiAppHandler {

    private final ApiAppGateway apiAppGateway;
    private final ApiAppConvertor apiAppConvertor;

    @Transactional(rollbackFor = Exception.class)
    public SingleResponse<ApiAppDTO> add(ApiAppAddCmd cmd) {
        ApiApp apiApp = ApiApp.create(cmd.getAppName(), cmd.getAllowedPaths());
        apiAppGateway.create(apiApp);
        return SingleResponse.of(apiAppConvertor.toDTO(apiApp));
    }

    @Transactional(readOnly = true)
    public MultiResponse<ApiAppDTO> list() {
        List<ApiApp> apiApps = apiAppGateway.findAll();
        List<ApiAppDTO> dtos = apiApps.stream()
                .map(apiAppConvertor::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtos);
    }

    @Transactional(readOnly = true)
    public SingleResponse<ApiAppDTO> getByApiKey(String apiKey) {
        ApiApp apiApp = apiAppGateway.findByApiKey(apiKey);
        if (apiApp == null) {
            return SingleResponse.buildFailure("NOT_FOUND", "API Key无效");
        }
        return SingleResponse.of(apiAppConvertor.toDTO(apiApp));
    }

    @Transactional(readOnly = true)
    public PageResponse<ApiAppDTO> page(ApiAppPageQry qry) {
        PageResult.validatePageSize(qry.getPageSize());
        return PageResult.map(apiAppGateway.page(qry), apiAppConvertor::toDTO);
    }
}
