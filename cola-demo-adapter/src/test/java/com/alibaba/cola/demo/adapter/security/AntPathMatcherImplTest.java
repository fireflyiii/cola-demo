package com.alibaba.cola.demo.adapter.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AntPathMatcherImpl 单元测试
 * 验证路径匹配和 URL 编码绕过防护
 */
class AntPathMatcherImplTest {

    private AntPathMatcherImpl pathMatcher;

    @BeforeEach
    void setUp() {
        pathMatcher = new AntPathMatcherImpl();
    }

    @Test
    void shouldMatchExactPath() {
        assertTrue(pathMatcher.isPathAllowed("/open/order/add", "/open/order/add"));
    }

    @Test
    void shouldNotMatchDifferentPath() {
        assertFalse(pathMatcher.isPathAllowed("/open/order/add", "/open/order/list"));
    }

    @Test
    void shouldMatchSingleLevelWildcard() {
        assertTrue(pathMatcher.isPathAllowed("/open/*", "/open/order"));
    }

    @Test
    void shouldNotMatchMultiLevelWithSingleWildcard() {
        assertFalse(pathMatcher.isPathAllowed("/open/*", "/open/order/add"));
    }

    @Test
    void shouldMatchMultiLevelWildcard() {
        assertTrue(pathMatcher.isPathAllowed("/open/**", "/open/order/add"));
        assertTrue(pathMatcher.isPathAllowed("/open/**", "/open/order"));
    }

    @Test
    void shouldMatchMultiplePatterns() {
        assertTrue(pathMatcher.isPathAllowed("/open/**,/customer/list", "/customer/list"));
        assertTrue(pathMatcher.isPathAllowed("/open/**,/customer/list", "/open/order"));
    }

    @Test
    void shouldDecodeUrlEncodedPath() {
        // %2F 编码绕过测试：/open/%2Forder 应被解码为 /open//order
        // 解码后 AntPathMatcher 匹配 /open/** 即可
        assertTrue(pathMatcher.isPathAllowed("/open/**", "/open/order%2Fadd"));
    }

    @Test
    void shouldReturnFalseForNullOrEmptyAllowedPaths() {
        assertFalse(pathMatcher.isPathAllowed(null, "/open/order"));
        assertFalse(pathMatcher.isPathAllowed("", "/open/order"));
        assertFalse(pathMatcher.isPathAllowed("  ", "/open/order"));
    }

    @Test
    void shouldHandlePathWithSpaces() {
        assertTrue(pathMatcher.isPathAllowed("/open/**", "/open/order add"));
    }

    @Test
    void shouldTrimPatternWhitespace() {
        assertTrue(pathMatcher.isPathAllowed("  /open/**  ,  /customer/list  ", "/open/order"));
    }
}
