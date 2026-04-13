package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.infrastructure.util.PageHelper;
import com.alibaba.cola.demo.client.dto.CustomerPageQry;
import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.domain.customer.gateway.CustomerGateway;
import com.alibaba.cola.demo.infrastructure.convertor.CustomerAssembler;
import com.alibaba.cola.demo.infrastructure.dataobject.CustomerEntity;
import com.alibaba.cola.demo.infrastructure.mapper.CustomerMapper;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户网关实现
 */
@Component
@RequiredArgsConstructor
public class CustomerGatewayImpl implements CustomerGateway {

    private final CustomerMapper customerMapper;
    private final CustomerAssembler customerAssembler;

    @Override
    public void create(Customer customer) {
        CustomerEntity customerEntity = customerAssembler.toEntity(customer);
        customerMapper.insert(customerEntity);
        customer.setCustomerId(customerEntity.getId());
    }

    @Override
    public List<Customer> listByName(String customerName) {
        LambdaQueryWrapper<CustomerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(CustomerEntity::getCustomerName, customerName);
        return customerMapper.selectList(wrapper).stream()
                .map(customerAssembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<Customer> pageByName(CustomerPageQry qry) {
        LambdaQueryWrapper<CustomerEntity> wrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(qry.getCustomerName())) {
            wrapper.like(CustomerEntity::getCustomerName, qry.getCustomerName());
        }
        return PageHelper.toPageResponse(
                customerMapper.selectPage(PageHelper.toPage(qry), wrapper),
                customerAssembler::toDomain
        );
    }
}
