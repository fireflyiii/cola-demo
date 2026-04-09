package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.dto.Command;

public class CustomerAddCmd extends Command {

    private String customerName;
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