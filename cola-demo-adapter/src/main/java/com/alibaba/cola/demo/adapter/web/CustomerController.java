package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.client.api.ICustomerService;
import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.demo.client.dto.CustomerPageQry;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final ICustomerService customerService;

    /**
     * 添加客户
     */
    @PostMapping("/add")
    public Response addCustomer(@RequestBody @Valid CustomerAddCmd cmd) {
        log.info("添加客户请求: customerName={}", cmd.getCustomerName());
        return customerService.addCustomer(cmd);
    }

    /**
     * 根据名称查询客户列表
     */
    @GetMapping("/list")
    public MultiResponse<CustomerDTO> listByName(@RequestParam String customerName) {
        log.info("查询客户列表请求: customerName={}", customerName);
        CustomerListByNameQry qry = new CustomerListByNameQry();
        qry.setCustomerName(customerName);
        return customerService.listByName(qry);
    }

    /**
     * 分页查询客户列表
     */
    @PostMapping("/page")
    public PageResponse<CustomerDTO> page(@RequestBody CustomerPageQry qry) {
        log.info("分页查询客户请求: customerName={}, pageIndex={}, pageSize={}",
                qry.getCustomerName(), qry.getPageIndex(), qry.getPageSize());
        return customerService.page(qry);
    }
}
