package com.alibaba.cola.demo.domain.customer.gateway;

import com.alibaba.cola.demo.domain.common.PageResult;
import com.alibaba.cola.demo.domain.customer.Customer;

import java.util.List;

public interface CustomerGateway {

    void create(Customer customer);

    List<Customer> listByName(String customerName);

    PageResult<Customer> pageByName(String customerName, int page, int pageSize);
}
