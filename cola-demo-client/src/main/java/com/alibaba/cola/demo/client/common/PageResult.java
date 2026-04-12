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
 * 提供分页对象的自动转换，消除中间对象
 */
public final class PageResult {

    private PageResult() {
    }

    /**
     * 将 COLA PageQuery 转换为 MyBatis-Plus Page 对象
     * Infrastructure 层可直接使用：PageResult.toPage(qry)
     *
     * @param qry 分页查询参数
     * @param <E> 实体类型
     * @return MyBatis-Plus Page 对象
     */
    public static <E> Page<E> toPage(PageQuery qry) {
        return new Page<>(qry.getPageIndex(), qry.getPageSize());
    }

    /**
     * 从 MyBatis-Plus IPage 直接转换为 COLA PageResponse，同时转换记录类型
     * Infrastructure 层可一步完成：PageResult.toPageResponse(iPage, converter)
     *
     * @param page      MyBatis-Plus 分页结果
     * @param converter 实体转换函数 (如 CustomerConvertor::toDomain)
     * @param <E>       数据库实体类型
     * @param <R>       目标类型
     * @return PageResponse
     */
    public static <E, R> PageResponse<R> toPageResponse(IPage<E> page, Function<E, R> converter) {
        List<R> data = page.getRecords().stream()
                .map(converter)
                .collect(Collectors.toList());
        return PageResponse.of(data, (int) page.getTotal(), (int) page.getSize(), (int) page.getCurrent());
    }

    /**
     * 对 PageResponse 中的记录进行二次转换
     * App 层可将 PageResponse<Domain> 转换为 PageResponse<DTO>
     *
     * @param pageResponse 分页响应
     * @param converter    记录转换函数 (如 CustomerConvertor::toDTO)
     * @param <T>          源类型
     * @param <R>          目标类型
     * @return PageResponse
     */
    public static <T, R> PageResponse<R> map(PageResponse<T> pageResponse, Function<T, R> converter) {
        List<R> data = pageResponse.getData().stream()
                .map(converter)
                .collect(Collectors.toList());
        return PageResponse.of(data, pageResponse.getTotalCount(), pageResponse.getPageSize(), pageResponse.getPageIndex());
    }
}
