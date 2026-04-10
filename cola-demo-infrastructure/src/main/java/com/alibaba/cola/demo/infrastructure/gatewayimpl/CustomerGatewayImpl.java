package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.domain.customer.gateway.CustomerGateway;
import com.alibaba.cola.demo.infrastructure.convertor.CustomerConvertor;
import com.alibaba.cola.demo.infrastructure.dataobject.CustomerEntity;
import com.alibaba.cola.demo.infrastructure.mapper.CustomerMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户网关实现
 */
@Component
public class CustomerGatewayImpl implements CustomerGateway {

    private final CustomerMapper customerMapper;

    public CustomerGatewayImpl(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    @Override
    public void create(Customer customer) {
        CustomerEntity customerEntity = CustomerConvertor.toEntity(customer);
        customerMapper.insert(customerEntity);
        customer.setCustomerId(customerEntity.getId());
    }

    @Override
    public List<Customer> listByName(String customerName) {
        LambdaQueryWrapper<CustomerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(CustomerEntity::getCustomerName, customerName);
        List<CustomerEntity> customerEntities = customerMapper.selectList(wrapper);
        return customerEntities.stream()
                .map(CustomerConvertor::toDomain)
                .collect(Collectors.toList());
    }
}
