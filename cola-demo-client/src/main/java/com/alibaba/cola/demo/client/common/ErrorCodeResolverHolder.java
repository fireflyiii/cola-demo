package com.alibaba.cola.demo.client.common;

/**
 * ErrorCodeResolver 持有者
 * 解决 Client 层不能直接依赖 Spring 的问题
 * Adapter 层启动时通过 setter 注入实现
 */
public class ErrorCodeResolverHolder {

    private static ErrorCodeResolver resolver;

    public static ErrorCodeResolver getResolver() {
        return resolver;
    }

    public static void setResolver(ErrorCodeResolver resolver) {
        ErrorCodeResolverHolder.resolver = resolver;
    }
}
