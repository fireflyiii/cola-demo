package com.alibaba.cola.demo.domain.customer;

import lombok.Data;

@Data
public class Customer {

    private Long customerId;
    private String customerName;
    private String companyType;

    /**
     * 领域行为：校验客户信息
     */
    public void validate() {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("客户名称不能为空");
        }
    }

    /**
     * 领域行为：判断是否为同一客户
     */
    public boolean isSameCustomer(Customer other) {
        return this.customerId != null && this.customerId.equals(other.getCustomerId());
    }
}