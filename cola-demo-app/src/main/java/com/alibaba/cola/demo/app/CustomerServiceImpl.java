package com.alibaba.cola.demo.app;

import com.alibaba.cola.demo.app.executor.CustomerAddCmdExe;
import com.alibaba.cola.demo.app.executor.query.CustomerListByNameQryExe;
import com.alibaba.cola.demo.client.api.ICustomerService;
import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerAddCmdExe customerAddCmdExe;
    private final CustomerListByNameQryExe customerListByNameQryExe;

    public CustomerServiceImpl(CustomerAddCmdExe customerAddCmdExe,
                               CustomerListByNameQryExe customerListByNameQryExe) {
        this.customerAddCmdExe = customerAddCmdExe;
        this.customerListByNameQryExe = customerListByNameQryExe;
    }

    @Override
    public Response addCustomer(CustomerAddCmd cmd) {
        return customerAddCmdExe.execute(cmd);
    }

    @Override
    public MultiResponse<CustomerDTO> listByName(CustomerListByNameQry qry) {
        return customerListByNameQryExe.execute(qry);
    }
}