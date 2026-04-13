package com.alibaba.cola.demo.domain.customer;

import com.alibaba.cola.demo.domain.enums.CompanyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompanyTypeTest {

    @Test
    void shouldReturnCorrectTypeFromCode() {
        assertEquals(CompanyType.PRIVATE, CompanyType.fromCode("PRIVATE"));
        assertEquals(CompanyType.STATE_OWNED, CompanyType.fromCode("STATE_OWNED"));
    }

    @Test
    void shouldReturnOtherForInvalidCode() {
        assertEquals(CompanyType.OTHER, CompanyType.fromCode("INVALID"));
    }

    @Test
    void shouldReturnNullForNullCode() {
        assertNull(CompanyType.fromCode(null));
    }
}
