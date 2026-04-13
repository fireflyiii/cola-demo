package com.alibaba.cola.demo.client.api;

import com.alibaba.cola.demo.client.dto.ApiAppAddCmd;
import com.alibaba.cola.demo.client.dto.ApiAppPageQry;
import com.alibaba.cola.demo.client.dto.data.ApiAppDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;

public interface IApiAppService {

    SingleResponse<ApiAppDTO> addApiApp(ApiAppAddCmd cmd);

    MultiResponse<ApiAppDTO> listApiApps();

    PageResponse<ApiAppDTO> pageApiApps(ApiAppPageQry qry);

    SingleResponse<ApiAppDTO> getByApiKey(String apiKey);
}
