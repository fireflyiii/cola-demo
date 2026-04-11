package com.alibaba.cola.demo.app.convertor;

import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.demo.domain.customer.Customer;

/**
 * 客户DTO转换器
 */
public class CustomerConvertor {

    private CustomerConvertor() {}

    /**
     * Domain转DTO
     */
    public static CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setCustomerName(customer.getCustomerName());
        dto.setCompanyType(customer.getCompanyType());
        return dto;
    }
}
