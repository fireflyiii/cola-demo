package com.alibaba.cola.demo.domain.customer.gateway;

import com.alibaba.cola.demo.domain.customer.Customer;

import java.util.List;

public interface CustomerGateway {

    void create(Customer customer);

    List<Customer> listByName(String customerName);
}