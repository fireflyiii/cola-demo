package com.alibaba.cola.demo.domain.common;

/**
 * Ant风格路径匹配工具
 * 支持 * 和 ** 通配符
 */
public final class PathMatcher {

    private PathMatcher() {
    }

    /**
     * 判断请求路径是否匹配允许的路径模式列表
     *
     * @param allowedPaths 逗号分隔的路径模式
     * @param requestPath  请求路径
     * @return 是否匹配
     */
    public static boolean isPathAllowed(String allowedPaths, String requestPath) {
        if (allowedPaths == null || allowedPaths.trim().isEmpty()) {
            return false;
        }
        for (String pattern : allowedPaths.split(",")) {
            if (matchPath(pattern.trim(), requestPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 简单的Ant路径匹配（支持 * 和 **）
     */
    public static boolean matchPath(String pattern, String path) {
        if (!pattern.contains("*")) {
            return pattern.equals(path);
        }
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            if (!path.startsWith(prefix)) {
                return false;
            }
            String remaining = path.substring(prefix.length());
            return !remaining.contains("/");
        }
        return false;
    }
}
