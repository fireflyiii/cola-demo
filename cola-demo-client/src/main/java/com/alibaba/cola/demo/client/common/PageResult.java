package com.alibaba.cola.demo.client.common;

import com.alibaba.cola.dto.PageResponse;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页结果映射工具（不依赖任何持久化框架）
 * App层使用：Domain -> DTO
 */
public final class PageResult {

    private PageResult() {
    }

    /**
     * PageResponse 记录类型映射
     *
     * @param pageResponse 分页响应
     * @param converter    记录转换函数 (如 convertor::toDTO)
     */
    public static <T, R> PageResponse<R> map(PageResponse<T> pageResponse, Function<T, R> converter) {
        List<R> data = pageResponse.getData().stream()
                .map(converter)
                .collect(Collectors.toList());
        return PageResponse.of(data, pageResponse.getTotalCount(), pageResponse.getPageSize(), pageResponse.getPageIndex());
    }
}
