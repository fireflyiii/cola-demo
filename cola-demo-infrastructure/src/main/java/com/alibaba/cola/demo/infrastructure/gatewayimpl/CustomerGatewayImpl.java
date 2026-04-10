package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.domain.customer.gateway.CustomerGateway;
import com.alibaba.cola.demo.infrastructure.convertor.CustomerConvertor;
import com.alibaba.cola.demo.infrastructure.dataobject.CustomerDO;
import com.alibaba.cola.demo.infrastructure.mapper.CustomerMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerGatewayImpl implements CustomerGateway {

    private final CustomerMapper customerMapper;

    public CustomerGatewayImpl(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    @Override
    public void create(Customer customer) {
        CustomerDO customerDO = CustomerConvertor.toDO(customer);
        customerMapper.insert(customerDO);
        customer.setCustomerId(customerDO.getId());
    }

    @Override
    public List<Customer> listByName(String customerName) {
        LambdaQueryWrapper<CustomerDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(CustomerDO::getCustomerName, customerName);
        List<CustomerDO> customerDOs = customerMapper.selectList(wrapper);
        return customerDOs.stream()
                .map(CustomerConvertor::toEntity)
                .collect(Collectors.toList());
    }
}