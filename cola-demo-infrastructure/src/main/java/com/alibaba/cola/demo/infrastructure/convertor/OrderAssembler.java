package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.order.Order;
import com.alibaba.cola.demo.domain.order.OrderStatus;
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
     * Entity转Domain（使用rebuild方法，从DB加载不触发创建校验）
     */
    default Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        return Order.rebuild(entity.getId(), entity.getOrderName(), entity.getAmount(),
                entity.getCustomerName(), OrderStatus.fromCode(entity.getStatus()));
    }
}
