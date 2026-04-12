package com.alibaba.cola.demo.app.service.impl;

import com.alibaba.cola.demo.app.executor.ApiAppHandler;
import com.alibaba.cola.demo.client.api.IApiAppService;
import com.alibaba.cola.demo.client.dto.ApiAppAddCmd;
import com.alibaba.cola.demo.client.dto.data.ApiAppDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiAppServiceImpl implements IApiAppService {

    private final ApiAppHandler apiAppHandler;

    @Override
    public SingleResponse<ApiAppDTO> addApiApp(ApiAppAddCmd cmd) {
        return apiAppHandler.add(cmd);
    }

    @Override
    public MultiResponse<ApiAppDTO> listApiApps() {
        return apiAppHandler.list();
    }

    @Override
    public SingleResponse<ApiAppDTO> getByApiKey(String apiKey) {
        return apiAppHandler.getByApiKey(apiKey);
    }
}
