package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;

import javax.validation.constraints.NotBlank;

public class CustomerAddCmd extends Command {

    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @NotBlank(message = "公司类型不能为空")
    private String companyType;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }
}