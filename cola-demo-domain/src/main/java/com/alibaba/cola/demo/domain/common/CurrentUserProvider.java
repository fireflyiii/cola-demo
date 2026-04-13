package com.alibaba.cola.demo.domain.common;

/**
 * 当前用户提供者（领域SPI）
 * 用于解耦基础设施层对Spring Security的直接依赖
 */
public interface CurrentUserProvider {

    /**
     * 获取当前登录用户名
     * @return 当前用户名，未登录时返回"system"
     */
    String getCurrentUsername();
}
