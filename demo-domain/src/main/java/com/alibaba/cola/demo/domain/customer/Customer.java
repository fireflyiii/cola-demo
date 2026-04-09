package com.alibaba.cola.demo.domain.customer;

import lombok.Data;

@Data
public class Customer {

    private Long customerId;
    private String customerName;
    private String companyType;
}