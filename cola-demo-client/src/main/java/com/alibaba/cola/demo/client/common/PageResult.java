package com.alibaba.cola.demo.client.common;

import com.alibaba.cola.dto.PageQuery;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页工具类
 * 桥接 COLA PageQuery/PageResponse 与 MyBatis-Plus IPage/Page
 *
 * 典型用法：
 * - Infrastructure层：PageResult.toPageResponse(mapper.selectPage(PageResult.toPage(qry), wrapper), assembler::toDomain)
 * - App层：PageResult.map(gateway.page(qry), convertor::toDTO)
 */
public final class PageResult {

    private PageResult() {
    }

    /**
     * COLA PageQuery -> MyBatis-Plus Page
     */
    public static <T> Page<T> toPage(PageQuery qry) {
        return new Page<>(qry.getPageIndex(), qry.getPageSize());
    }

    /**
     * MyBatis-Plus IPage -> COLA PageResponse
     * 一步完成分页结果转换 + 记录类型映射
     * Infrastructure层使用：Entity -> Domain
     *
     * @param page      MyBatis-Plus 分页结果
     * @param converter 记录转换函数 (如 assembler::toDomain)
     */
    public static <E, R> PageResponse<R> toPageResponse(IPage<E> page, Function<E, R> converter) {
        List<R> data = page.getRecords().stream()
                .map(converter)
                .collect(Collectors.toList());
        return PageResponse.of(data, (int) page.getTotal(), (int) page.getSize(), (int) page.getCurrent());
    }

    /**
     * PageResponse 记录类型映射
     * App层使用：Domain -> DTO
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
