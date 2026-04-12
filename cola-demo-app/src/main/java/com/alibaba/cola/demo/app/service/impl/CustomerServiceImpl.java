package com.alibaba.cola.demo.app.service.impl;

import com.alibaba.cola.demo.app.executor.CustomerHandler;
import com.alibaba.cola.demo.client.api.ICustomerService;
import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.demo.client.dto.CustomerPageQry;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerHandler customerHandler;

    @Override
    public Response addCustomer(CustomerAddCmd cmd) {
        return customerHandler.add(cmd);
    }

    @Override
    public MultiResponse<CustomerDTO> listByName(CustomerListByNameQry qry) {
        return customerHandler.listByName(qry);
    }

    @Override
    public PageResponse<CustomerDTO> page(CustomerPageQry qry) {
        return customerHandler.page(qry);
    }
}
