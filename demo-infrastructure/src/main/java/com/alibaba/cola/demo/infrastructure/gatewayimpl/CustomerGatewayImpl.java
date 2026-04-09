package com.alibaba.cola.demo.infrastructure.gatewayimpl;

import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.domain.customer.gateway.CustomerGateway;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class CustomerGatewayImpl implements CustomerGateway {

    private final ConcurrentHashMap<Long, Customer> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public void create(Customer customer) {
        customer.setCustomerId(idGenerator.getAndIncrement());
        store.put(customer.getCustomerId(), customer);
    }

    @Override
    public List<Customer> listByName(String customerName) {
        List<Customer> result = new ArrayList<>();
        for (Customer c : store.values()) {
            if (customerName == null || c.getCustomerName().contains(customerName)) {
                result.add(c);
            }
        }
        return result;
    }
}