package com.alibaba.cola.demo.infrastructure.convertor;

import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.domain.customer.CompanyType;
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
     * Entity转Domain（使用rebuild方法，从DB加载不触发创建校验）
     */
    default Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        return Customer.rebuild(entity.getId(), entity.getCustomerName(),
                CompanyType.fromCode(entity.getCompanyType()));
    }
}
