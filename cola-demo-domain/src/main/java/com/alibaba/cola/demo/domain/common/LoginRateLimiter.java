package com.alibaba.cola.demo.domain.common;

/**
 * 登录限流器接口
 * 领域层定义接口，基础设施层提供实现
 */
public interface LoginRateLimiter {

    /**
     * 检查是否允许登录
     *
     * @param username 用户名
     * @return true=允许，false=被限流
     */
    boolean allowLogin(String username);

    /**
     * 登录成功后清除计数
     */
    void clearAttempts(String username);

    /**
     * 获取剩余锁定时间（秒）
     */
    long getRemainingLockTime(String username);
}
