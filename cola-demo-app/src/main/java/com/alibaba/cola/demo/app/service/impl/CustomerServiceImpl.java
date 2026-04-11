package com.alibaba.cola.demo.app.service.impl;

import com.alibaba.cola.demo.app.executor.CustomerAddHandler;
import com.alibaba.cola.demo.app.executor.query.CustomerListByNameHandler;
import com.alibaba.cola.demo.app.executor.query.CustomerPageHandler;
import com.alibaba.cola.demo.client.api.ICustomerService;
import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.demo.client.dto.CustomerPageQry;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerAddHandler customerAddHandler;
    @Autowired
    private CustomerListByNameHandler customerListByNameHandler;
    @Autowired
    private CustomerPageHandler customerPageHandler;

    @Override
    public Response addCustomer(CustomerAddCmd cmd) {
        return customerAddHandler.execute(cmd);
    }

    @Override
    public MultiResponse<CustomerDTO> listByName(CustomerListByNameQry qry) {
        return customerListByNameHandler.execute(qry);
    }

    @Override
    public PageResponse<CustomerDTO> page(CustomerPageQry qry) {
        return customerPageHandler.execute(qry);
    }
}
