package com.alibaba.cola.demo.app.executor.query;

import com.alibaba.cola.demo.app.convertor.CustomerConvertor;
import com.alibaba.cola.demo.client.dto.CustomerPageQry;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.domain.customer.gateway.CustomerGateway;
import com.alibaba.cola.demo.domain.common.PageResult;
import com.alibaba.cola.dto.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerPageHandler {

    @Autowired
    private CustomerGateway customerGateway;

    @Transactional(readOnly = true)
    public PageResponse<CustomerDTO> execute(CustomerPageQry qry) {
        int page = qry.getPageIndex();
        int pageSize = qry.getPageSize();

        PageResult<Customer> pageResult = customerGateway.pageByName(qry.getCustomerName(), page, pageSize);

        List<CustomerDTO> dtos = pageResult.getRecords().stream()
                .map(CustomerConvertor::toDTO)
                .collect(Collectors.toList());

        return PageResponse.of(dtos, (int) pageResult.getTotal(), pageSize, page);
    }
}
