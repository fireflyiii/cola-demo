package com.alibaba.cola.demo.startup;

import com.alibaba.cola.demo.client.api.ICustomerService;
import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceIntegrationTest {

    @Autowired
    private ICustomerService customerService;

    @Test
    void testAddAndListCustomer() {
        CustomerAddCmd cmd = new CustomerAddCmd();
        cmd.setCustomerName("IntegrationTest");
        cmd.setCompanyType("IT");
        Response response = customerService.addCustomer(cmd);
        assertTrue(response.isSuccess());

        CustomerListByNameQry qry = new CustomerListByNameQry();
        qry.setCustomerName("Integration");
        MultiResponse<CustomerDTO> result = customerService.listByName(qry);
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
    }
}