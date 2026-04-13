package com.alibaba.cola.demo.infrastructure.util;

import com.alibaba.cola.dto.PageQuery;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MyBatis-Plus 分页桥接工具
 * 仅在 Infrastructure 层使用，不暴露到 Client 层
 *
 * 典型用法：
 * PageHelper.toPageResponse(mapper.selectPage(PageHelper.toPage(qry), wrapper), assembler::toDomain)
 */
public final class PageHelper {

    private PageHelper() {
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
}
