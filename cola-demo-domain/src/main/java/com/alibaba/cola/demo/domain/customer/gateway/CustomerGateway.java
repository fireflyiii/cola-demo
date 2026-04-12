package com.alibaba.cola.demo.domain.customer.gateway;

import com.alibaba.cola.demo.client.dto.CustomerPageQry;
import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.dto.PageResponse;

import java.util.List;

public interface CustomerGateway {

    void create(Customer customer);

    List<Customer> listByName(String customerName);

    PageResponse<Customer> pageByName(CustomerPageQry qry);
}
