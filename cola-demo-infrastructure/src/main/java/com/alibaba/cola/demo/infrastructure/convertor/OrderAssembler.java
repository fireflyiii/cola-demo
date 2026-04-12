package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.order.Order;
import com.alibaba.cola.demo.infrastructure.dataobject.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 订单装配器（Entity <-> Domain）
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderAssembler {

    /**
     * Domain转Entity（自动生成，不映射id，由数据库生成）
     */
    OrderEntity toEntity(Order order);

    /**
     * Entity转Domain（使用工厂方法，保留领域校验）
     */
    default Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        Order order = Order.create(entity.getOrderName(), entity.getAmount(), entity.getCustomerName());
        order.setOrderId(entity.getId());
        order.setStatus(entity.getStatus());
        return order;
    }
}
