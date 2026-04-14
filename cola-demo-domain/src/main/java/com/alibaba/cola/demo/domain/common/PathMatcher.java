package com.alibaba.cola.demo.domain.common;

/**
 * 路径匹配接口
 * 领域层定义接口，适配器层提供实现
 * 支持基于 Ant 风格的路径匹配
 */
public interface PathMatcher {

    /**
     * 判断请求路径是否匹配允许的路径模式列表
     *
     * @param allowedPaths 逗号分隔的路径模式
     * @param requestPath  请求路径
     * @return 是否匹配
     */
    boolean isPathAllowed(String allowedPaths, String requestPath);
}
