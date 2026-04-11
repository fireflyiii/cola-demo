package com.alibaba.cola.demo.app.executor.query;

import com.alibaba.cola.demo.app.convertor.CustomerConvertor;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.domain.customer.gateway.CustomerGateway;
import com.alibaba.cola.dto.MultiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询Handler
 * 演示只读事务的使用
 */
@Component
public class CustomerListByNameHandler {

    @Autowired
    private CustomerGateway customerGateway;

    /**
     * 查询操作使用只读事务优化性能
     * readOnly = true 表示不修改数据，Spring会进行优化
     */
    @Transactional(readOnly = true)
    public MultiResponse<CustomerDTO> execute(CustomerListByNameQry qry) {
        List<Customer> customers = customerGateway.listByName(qry.getCustomerName());
        List<CustomerDTO> dtos = customers.stream()
                .map(CustomerConvertor::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtos);
    }
}
