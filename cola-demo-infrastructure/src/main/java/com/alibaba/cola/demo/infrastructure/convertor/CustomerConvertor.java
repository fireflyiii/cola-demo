package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.infrastructure.dataobject.CustomerEntity;

/**
 * 客户转换器
 */
public class CustomerConvertor {

    private CustomerConvertor() {}

    /**
     * Entity转Domain
     */
    public static Customer toDomain(CustomerEntity customerEntity) {
        if (customerEntity == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setCustomerId(customerEntity.getId());
        customer.setCustomerName(customerEntity.getCustomerName());
        customer.setCompanyType(customerEntity.getCompanyType());
        return customer;
    }

    /**
     * Domain转Entity
     */
    public static CustomerEntity toEntity(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customer.getCustomerId());
        customerEntity.setCustomerName(customer.getCustomerName());
        customerEntity.setCompanyType(customer.getCompanyType());
        return customerEntity;
    }
}
