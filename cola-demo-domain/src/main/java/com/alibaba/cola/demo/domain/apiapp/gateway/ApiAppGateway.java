package com.alibaba.cola.demo.domain.apiapp.gateway;

import com.alibaba.cola.demo.domain.apiapp.ApiApp;

import java.util.List;

public interface ApiAppGateway {

    void create(ApiApp apiApp);

    ApiApp findByApiKey(String apiKey);

    List<ApiApp> findAll();
}
