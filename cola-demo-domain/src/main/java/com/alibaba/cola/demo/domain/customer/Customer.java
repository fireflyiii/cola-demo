package com.alibaba.cola.demo.domain.customer;

import lombok.Getter;

/**
 * 客户领域实体
 */
@Getter
public class Customer {

    private Long customerId;
    private String customerName;
    private String companyType;

    /**
     * 创建客户
     */
    public static Customer create(String customerName, String companyType) {
        Customer customer = new Customer();
        customer.customerName = customerName;
        customer.companyType = companyType;
        customer.validate();
        return customer;
    }

    /**
     * 领域行为：校验客户信息
     */
    public void validate() {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("客户名称不能为空");
        }
    }

    /**
     * 设置客户ID（由Gateway在持久化后调用）
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * 判断是否为同一客户
     */
    public boolean isSameCustomer(Customer other) {
        return this.customerId != null && this.customerId.equals(other.getCustomerId());
    }
}
