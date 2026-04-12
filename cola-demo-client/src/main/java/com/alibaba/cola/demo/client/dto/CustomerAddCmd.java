package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerAddCmd extends Command {

    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @NotBlank(message = "公司类型不能为空")
    private String companyType;
}
