package com.alibaba.cola.demo.domain.customer;

import com.alibaba.cola.demo.client.common.DomainException;
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
    void shouldSetCustomerId() {
        Customer customer = Customer.create("张三", "PRIVATE");
        customer.setCustomerId(1L);

        assertEquals(1L, customer.getCustomerId());
    }

    @Test
    void shouldBeSameCustomerWhenIdEquals() {
        Customer c1 = Customer.create("张三", "PRIVATE");
        c1.setCustomerId(1L);
        Customer c2 = Customer.create("李四", "STATE_OWNED");
        c2.setCustomerId(1L);

        assertTrue(c1.isSameCustomer(c2));
    }

    @Test
    void shouldNotBeSameCustomerWhenIdDiffers() {
        Customer c1 = Customer.create("张三", "PRIVATE");
        c1.setCustomerId(1L);
        Customer c2 = Customer.create("张三", "PRIVATE");
        c2.setCustomerId(2L);

        assertFalse(c1.isSameCustomer(c2));
    }
}
