package com.alibaba.cola.demo.client.api;

import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.demo.client.dto.CustomerPageQry;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.dto.PageResponse;

public interface ICustomerService {

    Response addCustomer(CustomerAddCmd cmd);

    MultiResponse<CustomerDTO> listByName(CustomerListByNameQry qry);

    PageResponse<CustomerDTO> page(CustomerPageQry qry);
}