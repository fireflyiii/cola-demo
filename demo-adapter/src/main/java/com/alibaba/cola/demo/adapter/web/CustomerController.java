package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.client.api.CustomerServiceI;
import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerServiceI customerService;

    public CustomerController(CustomerServiceI customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public Response addCustomer(@RequestBody CustomerAddCmd cmd) {
        return customerService.addCustomer(cmd);
    }

    @GetMapping
    public MultiResponse<CustomerDTO> listByName(@RequestParam String customerName) {
        CustomerListByNameQry qry = new CustomerListByNameQry();
        qry.setCustomerName(customerName);
        return customerService.listByName(qry);
    }
}