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

    /**
     * pageSize 最大值限制，防止恶意请求导致 OOM
     */
    public static final int MAX_PAGE_SIZE = 100;

    private PageResult() {
    }

    /**
     * 校验分页参数
     *
     * @param pageSize 每页条数
     * @throws DomainException 当 pageSize 超过最大限制时
     */
    public static void validatePageSize(int pageSize) {
        if (pageSize > MAX_PAGE_SIZE) {
            throw new DomainException(BizErrorCode.B_PAGE_SIZE_EXCEEDED);
        }
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
