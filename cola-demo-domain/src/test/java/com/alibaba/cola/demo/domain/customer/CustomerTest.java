package com.alibaba.cola.demo.domain.customer;

import com.alibaba.cola.demo.client.common.DomainException;
import com.alibaba.cola.demo.domain.enums.CompanyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void shouldCreateCustomerWhenValid() {
        Customer customer = Customer.create("张三", "PRIVATE");

        assertEquals("张三", customer.getCustomerName());
        assertEquals(CompanyType.PRIVATE, customer.getCompanyType());
        assertNull(customer.getCustomerId());
    }

    @Test
    void shouldThrowWhenCustomerNameIsBlank() {
        assertThrows(DomainException.class, () -> Customer.create("", "PRIVATE"));
    }

    @Test
    void shouldThrowWhenCustomerNameIsNull() {
        assertThrows(DomainException.class, () -> Customer.create(null, "PRIVATE"));
    }

    @Test
    void shouldRebuildCustomerWithId() {
        Customer customer = Customer.rebuild(1L, "张三", CompanyType.PRIVATE);

        assertEquals(1L, customer.getCustomerId());
        assertEquals("张三", customer.getCustomerName());
    }

    @Test
    void shouldBeSameCustomerWhenIdEquals() {
        Customer c1 = Customer.rebuild(1L, "张三", CompanyType.PRIVATE);
        Customer c2 = Customer.rebuild(1L, "李四", CompanyType.STATE_OWNED);

        assertTrue(c1.isSameCustomer(c2));
    }

    @Test
    void shouldNotBeSameCustomerWhenIdDiffers() {
        Customer c1 = Customer.rebuild(1L, "张三", CompanyType.PRIVATE);
        Customer c2 = Customer.rebuild(2L, "张三", CompanyType.PRIVATE);

        assertFalse(c1.isSameCustomer(c2));
    }
}
