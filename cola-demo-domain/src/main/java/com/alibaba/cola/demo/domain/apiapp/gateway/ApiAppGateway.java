package com.alibaba.cola.demo.domain.apiapp.gateway;

import com.alibaba.cola.demo.client.dto.ApiAppPageQry;
import com.alibaba.cola.demo.domain.apiapp.ApiApp;
import com.alibaba.cola.dto.PageResponse;

import java.util.List;

/**
 * API应用网关接口
 */
public interface ApiAppGateway {

    void create(ApiApp apiApp);

    ApiApp findByApiKey(String apiKey);

    List<ApiApp> findAll();

    PageResponse<ApiApp> page(ApiAppPageQry qry);
}
