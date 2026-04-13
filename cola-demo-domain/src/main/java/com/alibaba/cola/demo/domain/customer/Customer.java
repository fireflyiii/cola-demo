package com.alibaba.cola.demo.domain.customer;

import com.alibaba.cola.demo.client.common.BizErrorCode;
import com.alibaba.cola.demo.client.common.DomainException;
import com.alibaba.cola.demo.domain.common.AggregateRoot;
import com.alibaba.cola.demo.domain.enums.CompanyType;
import lombok.Getter;

/**
 * 客户聚合根
 */
@Getter
public class Customer implements AggregateRoot {

    private Long customerId;
    private String customerName;
    private CompanyType companyType;

    /**
     * 创建客户
     */
    public static Customer create(String customerName, String companyType) {
        Customer customer = new Customer();
        customer.customerName = customerName;
        customer.companyType = CompanyType.fromCode(companyType);
        customer.validate();
        return customer;
    }

    /**
     * 重建客户实体（由Assembler从持久化层加载时使用）
     */
    public static Customer rebuild(Long customerId, String customerName, CompanyType companyType) {
        Customer customer = new Customer();
        customer.customerId = customerId;
        customer.customerName = customerName;
        customer.companyType = companyType;
        return customer;
    }

    /**
     * 领域行为：校验客户信息
     */
    public void validate() {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new DomainException(BizErrorCode.B_CUSTOMER_NAME_NOT_BLANK);
        }
    }

    /**
     * 设置客户ID（由Gateway在持久化后回填主键）
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * 判断是否为同一客户
     */
    public boolean isSameCustomer(Customer other) {
        return other != null && this.customerId != null && this.customerId.equals(other.getCustomerId());
    }
}
