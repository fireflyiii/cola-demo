package com.alibaba.cola.demo.app.executor;

import com.alibaba.cola.demo.app.convertor.CustomerConvertor;
import com.alibaba.cola.demo.client.common.PageResult;
import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.demo.client.dto.CustomerPageQry;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.demo.domain.common.DomainEventPublisher;
import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.domain.customer.CustomerCreatedEvent;
import com.alibaba.cola.demo.domain.customer.gateway.CustomerGateway;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerHandler {

    private final CustomerGateway customerGateway;
    private final CustomerConvertor customerConvertor;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional(rollbackFor = Exception.class)
    public Response add(CustomerAddCmd cmd) {
        Customer customer = Customer.create(cmd.getCustomerName(), cmd.getCompanyType());
        customerGateway.create(customer);
        domainEventPublisher.publish(new CustomerCreatedEvent(customer.getCustomerId(), customer.getCustomerName()));
        return Response.buildSuccess();
    }

    @Transactional(readOnly = true)
    public MultiResponse<CustomerDTO> listByName(CustomerListByNameQry qry) {
        List<Customer> customers = customerGateway.listByName(qry.getCustomerName());
        List<CustomerDTO> dtos = customers.stream()
                .map(customerConvertor::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtos);
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerDTO> page(CustomerPageQry qry) {
        PageResult.validatePageSize(qry.getPageSize());
        return PageResult.map(customerGateway.pageByName(qry), customerConvertor::toDTO);
    }
}
