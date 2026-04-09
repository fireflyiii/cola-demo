package com.alibaba.cola.demo.app;

import com.alibaba.cola.demo.client.api.CustomerServiceI;
import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.domain.customer.gateway.CustomerGateway;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerServiceI {

    private final CustomerGateway customerGateway;

    public CustomerServiceImpl(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    @Override
    public Response addCustomer(CustomerAddCmd cmd) {
        Customer customer = new Customer();
        customer.setCustomerName(cmd.getCustomerName());
        customer.setCompanyType(cmd.getCompanyType());
        customerGateway.create(customer);
        return Response.buildSuccess();
    }

    @Override
    public MultiResponse<CustomerDTO> listByName(CustomerListByNameQry qry) {
        List<Customer> customers = customerGateway.listByName(qry.getCustomerName());
        List<CustomerDTO> dtos = new ArrayList<>();
        for (Customer c : customers) {
            CustomerDTO dto = new CustomerDTO();
            dto.setCustomerId(c.getCustomerId());
            dto.setCustomerName(c.getCustomerName());
            dto.setCompanyType(c.getCompanyType());
            dtos.add(dto);
        }
        return MultiResponse.of(dtos);
    }
}