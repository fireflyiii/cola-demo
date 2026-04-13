package com.alibaba.cola.demo.infrastructure.cache;

/**
 * 缓存键常量
 */
public final class CacheKeys {

    private CacheKeys() {
    }

    /** API应用缓存 key 前缀，后接 apiKey */
    public static final String API_APP_PREFIX = "cache:apiapp:key:";
    /** API应用缓存过期时间（秒） */
    public static final long API_APP_TTL_SECONDS = 300;

    /** 用户认证信息缓存 key 前缀，后接 username */
    public static final String USER_AUTH_PREFIX = "cache:user:auth:";
    /** 用户认证信息缓存过期时间（秒） */
    public static final long USER_AUTH_TTL_SECONDS = 300;

    /** 角色列表缓存 key */
    public static final String ROLE_LIST = "cache:role:list";
    /** 角色列表缓存过期时间（秒） */
    public static final long ROLE_LIST_TTL_SECONDS = 600;

    /** 权限列表缓存 key */
    public static final String PERMISSION_LIST = "cache:permission:list";
    /** 权限列表缓存过期时间（秒） */
    public static final long PERMISSION_LIST_TTL_SECONDS = 600;

    /** 用户角色编码缓存 key 前缀，后接 username */
    public static final String USER_ROLE_CODES_PREFIX = "cache:user:rolecodes:";
    /** 用户权限编码缓存 key 前缀，后接 roleCodes hash */
    public static final String USER_PERMISSION_CODES_PREFIX = "cache:user:permcodes:";
}
