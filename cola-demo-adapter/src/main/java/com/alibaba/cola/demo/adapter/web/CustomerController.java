package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.client.api.ICustomerService;
import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/add")
    public Response addCustomer(@RequestBody @Valid CustomerAddCmd cmd) {
        return customerService.addCustomer(cmd);
    }

    @GetMapping("/list")
    public MultiResponse<CustomerDTO> listByName(@RequestParam String customerName) {
        CustomerListByNameQry qry = new CustomerListByNameQry();
        qry.setCustomerName(customerName);
        return customerService.listByName(qry);
    }
}