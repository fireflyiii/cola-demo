package com.alibaba.cola.demo.client.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTest {

    @Test
    void shouldContainErrorCodeAndMessage() {
        DomainException ex = new DomainException("ERR_001", "something went wrong");

        assertEquals("ERR_001", ex.getErrCode());
        assertEquals("something went wrong", ex.getMessage());
    }

    @Test
    void shouldCreateFromBizErrorCode() {
        DomainException ex = new DomainException(BizErrorCode.B_CUSTOMER_NAME_NOT_BLANK);

        assertEquals("B_CUSTOMER_NAME_NOT_BLANK", ex.getErrCode());
        assertEquals("客户名称不能为空", ex.getMessage());
    }

    @Test
    void shouldPreserveCause() {
        RuntimeException cause = new RuntimeException("root cause");
        DomainException ex = new DomainException("ERR_002", "wrapper", cause);

        assertEquals("ERR_002", ex.getErrCode());
        assertSame(cause, ex.getCause());
    }

    @Test
    void shouldPreserveCauseFromBizErrorCode() {
        RuntimeException cause = new RuntimeException("root cause");
        DomainException ex = new DomainException(BizErrorCode.B_USER_DISABLED, cause);

        assertEquals("B_USER_DISABLED", ex.getErrCode());
        assertEquals("用户已被禁用", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
