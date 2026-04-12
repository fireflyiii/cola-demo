package com.alibaba.cola.demo.app.convertor;

import com.alibaba.cola.demo.client.dto.data.OrderDTO;
import com.alibaba.cola.demo.domain.order.Order;
import org.mapstruct.Mapper;

/**
 * 订单DTO转换器
 */
@Mapper(componentModel = "spring")
public interface OrderConvertor {

    OrderDTO toDTO(Order order);
}
