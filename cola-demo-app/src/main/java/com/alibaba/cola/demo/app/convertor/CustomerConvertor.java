package com.alibaba.cola.demo.app.convertor;

import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.demo.domain.customer.Customer;
import org.mapstruct.Mapper;

/**
 * 客户DTO转换器
 */
@Mapper(componentModel = "spring")
public interface CustomerConvertor {

    CustomerDTO toDTO(Customer customer);
}
