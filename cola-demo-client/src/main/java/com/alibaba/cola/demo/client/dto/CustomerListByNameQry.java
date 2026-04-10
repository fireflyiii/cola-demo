package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Query;

public class CustomerListByNameQry extends Query {

    private String customerName;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}