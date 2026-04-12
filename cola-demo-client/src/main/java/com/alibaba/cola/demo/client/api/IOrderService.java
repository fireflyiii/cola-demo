package com.alibaba.cola.demo.client.api;

import com.alibaba.cola.demo.client.dto.OrderAddCmd;
import com.alibaba.cola.demo.client.dto.OrderListByNameQry;
import com.alibaba.cola.demo.client.dto.data.OrderDTO;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;

public interface IOrderService {

    Response addOrder(OrderAddCmd cmd);

    MultiResponse<OrderDTO> listByName(OrderListByNameQry qry);
}
