package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.client.api.IApiAppService;
import com.alibaba.cola.demo.client.common.OperationLog;
import com.alibaba.cola.demo.client.dto.ApiAppAddCmd;
import com.alibaba.cola.demo.client.dto.ApiAppPageQry;
import com.alibaba.cola.demo.client.dto.data.ApiAppDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * API应用管理控制器（仅管理员可操作）
 */
@RestController
@RequestMapping("/api/v1/api-app")
@RequiredArgsConstructor
public class ApiAppController {

    private final IApiAppService apiAppService;

    /**
     * 创建API应用，返回生成的API Key
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(type = "新增", description = "创建API应用")
    public SingleResponse<ApiAppDTO> addApiApp(@RequestBody @Valid ApiAppAddCmd cmd) {
        return apiAppService.addApiApp(cmd);
    }

    /**
     * 查询所有API应用
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public MultiResponse<ApiAppDTO> listApiApps() {
        return apiAppService.listApiApps();
    }

    /**
     * 分页查询API应用
     */
    @PostMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<ApiAppDTO> pageApiApps(@RequestBody ApiAppPageQry qry) {
        return apiAppService.pageApiApps(qry);
    }
}
