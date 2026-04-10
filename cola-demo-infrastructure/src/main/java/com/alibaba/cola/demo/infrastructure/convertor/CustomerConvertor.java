package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.infrastructure.dataobject.CustomerDO;

public class CustomerConvertor {

    public static Customer toEntity(CustomerDO customerDO) {
        Customer customer = new Customer();
        customer.setCustomerId(customerDO.getId());
        customer.setCustomerName(customerDO.getCustomerName());
        customer.setCompanyType(customerDO.getCompanyType());
        return customer;
    }

    public static CustomerDO toDO(Customer customer) {
        CustomerDO customerDO = new CustomerDO();
        customerDO.setId(customer.getCustomerId());
        customerDO.setCustomerName(customer.getCustomerName());
        customerDO.setCompanyType(customer.getCompanyType());
        return customerDO;
    }
}