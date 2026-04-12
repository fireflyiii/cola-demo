package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.infrastructure.dataobject.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 客户装配器（Entity <-> Domain）
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerAssembler {

    /**
     * Domain转Entity（自动生成，不映射id，由数据库生成）
     */
    CustomerEntity toEntity(Customer customer);

    /**
     * Entity转Domain（使用工厂方法，保留领域校验）
     */
    default Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        Customer customer = Customer.create(entity.getCustomerName(), entity.getCompanyType());
        customer.setCustomerId(entity.getId());
        return customer;
    }
}
