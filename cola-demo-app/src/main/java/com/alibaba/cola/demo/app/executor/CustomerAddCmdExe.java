package com.alibaba.cola.demo.app.executor;

import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.domain.customer.gateway.CustomerGateway;
import com.alibaba.cola.dto.Response;
import org.springframework.stereotype.Component;

@Component
public class CustomerAddCmdExe {

    private final CustomerGateway customerGateway;

    public CustomerAddCmdExe(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    public Response execute(CustomerAddCmd cmd) {
        Customer customer = new Customer();
        customer.setCustomerName(cmd.getCustomerName());
        customer.setCompanyType(cmd.getCompanyType());
        customerGateway.create(customer);
        return Response.buildSuccess();
    }
}