package com.alibaba.cola.demo.domain.common;

/**
 * JWT Token黑名单接口
 * 领域层定义接口，基础设施层提供实现
 */
public interface TokenBlacklist {

    /**
     * 将Token加入黑名单
     *
     * @param token           JWT Token
     * @param remainingSeconds 剩余有效时间（秒）
     */
    void add(String token, long remainingSeconds);

    /**
     * 检查Token是否在黑名单中
     */
    boolean contains(String token);
}
