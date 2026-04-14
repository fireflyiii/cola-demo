package com.alibaba.cola.demo.client.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageResult.validatePageSize 单元测试
 * 验证 pageSize 上限校验逻辑
 */
class PageResultTest {

    @Test
    void shouldPassWhenPageSizeWithinLimit() {
        assertDoesNotThrow(() -> PageResult.validatePageSize(1));
        assertDoesNotThrow(() -> PageResult.validatePageSize(50));
        assertDoesNotThrow(() -> PageResult.validatePageSize(100));
    }

    @Test
    void shouldThrowWhenPageSizeExceedsLimit() {
        DomainException ex = assertThrows(DomainException.class,
                () -> PageResult.validatePageSize(101));
        assertEquals(BizErrorCode.B_PAGE_SIZE_EXCEEDED.getErrCode(), ex.getErrCode());
    }

    @Test
    void shouldThrowForVeryLargePageSize() {
        assertThrows(DomainException.class,
                () -> PageResult.validatePageSize(Integer.MAX_VALUE));
    }

    @Test
    void shouldAllowZeroPageSize() {
        // pageSize=0 由框架处理（返回空页），不视为超限
        assertDoesNotThrow(() -> PageResult.validatePageSize(0));
    }
}
