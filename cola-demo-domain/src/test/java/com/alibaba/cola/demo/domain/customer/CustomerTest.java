package com.alibaba.cola.demo.domain.customer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void shouldCreateCustomerWhenValid() {
        Customer customer = Customer.create("张三", "PERSON");

        assertEquals("张三", customer.getCustomerName());
        assertEquals("PERSON", customer.getCompanyType());
        assertNull(customer.getCustomerId());
    }

    @Test
    void shouldThrowWhenCustomerNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> Customer.create("", "PERSON"));
    }

    @Test
    void shouldThrowWhenCustomerNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> Customer.create(null, "PERSON"));
    }

    @Test
    void shouldSetCustomerId() {
        Customer customer = Customer.create("张三", "PERSON");
        customer.setCustomerId(1L);

        assertEquals(1L, customer.getCustomerId());
    }

    @Test
    void shouldBeSameCustomerWhenIdEquals() {
        Customer c1 = Customer.create("张三", "PERSON");
        c1.setCustomerId(1L);
        Customer c2 = Customer.create("李四", "COMPANY");
        c2.setCustomerId(1L);

        assertTrue(c1.isSameCustomer(c2));
    }

    @Test
    void shouldNotBeSameCustomerWhenIdDiffers() {
        Customer c1 = Customer.create("张三", "PERSON");
        c1.setCustomerId(1L);
        Customer c2 = Customer.create("张三", "PERSON");
        c2.setCustomerId(2L);

        assertFalse(c1.isSameCustomer(c2));
    }
}
