package com.alibaba.cola.demo.client.dto.data;

import lombok.Data;

@Data
public class CustomerDTO {

    private Long customerId;
    private String customerName;
    private String companyType;
}