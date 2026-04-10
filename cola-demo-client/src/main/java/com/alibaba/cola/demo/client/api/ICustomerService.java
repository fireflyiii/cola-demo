package com.alibaba.cola.demo.client.api;

import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;

public interface ICustomerService {

    Response addCustomer(CustomerAddCmd cmd);

    MultiResponse<CustomerDTO> listByName(CustomerListByNameQry qry);
}