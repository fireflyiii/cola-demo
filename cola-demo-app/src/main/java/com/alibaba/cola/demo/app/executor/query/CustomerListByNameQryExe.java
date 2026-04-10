package com.alibaba.cola.demo.app.executor.query;

import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.domain.customer.gateway.CustomerGateway;
import com.alibaba.cola.dto.MultiResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerListByNameQryExe {

    private final CustomerGateway customerGateway;

    public CustomerListByNameQryExe(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    public MultiResponse<CustomerDTO> execute(CustomerListByNameQry qry) {
        List<Customer> customers = customerGateway.listByName(qry.getCustomerName());
        List<CustomerDTO> dtos = customers.stream().map(c -> {
            CustomerDTO dto = new CustomerDTO();
            dto.setCustomerId(c.getCustomerId());
            dto.setCustomerName(c.getCustomerName());
            dto.setCompanyType(c.getCompanyType());
            return dto;
        }).collect(Collectors.toList());
        return MultiResponse.of(dtos);
    }
}