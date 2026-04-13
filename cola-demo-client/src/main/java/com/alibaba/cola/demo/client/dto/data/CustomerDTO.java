package com.alibaba.cola.demo.client.dto.data;

import com.alibaba.cola.dto.DTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerDTO extends DTO {

    private Long customerId;
    private String customerName;
    private String companyType;
}