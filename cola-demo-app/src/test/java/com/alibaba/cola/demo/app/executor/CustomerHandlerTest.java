package com.alibaba.cola.demo.app.executor;

import com.alibaba.cola.demo.app.convertor.CustomerConvertor;
import com.alibaba.cola.demo.client.dto.CustomerAddCmd;
import com.alibaba.cola.demo.client.dto.CustomerListByNameQry;
import com.alibaba.cola.demo.client.dto.data.CustomerDTO;
import com.alibaba.cola.demo.domain.common.DomainEventPublisher;
import com.alibaba.cola.demo.domain.customer.Customer;
import com.alibaba.cola.demo.domain.customer.CompanyType;
import com.alibaba.cola.demo.domain.customer.gateway.CustomerGateway;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CustomerHandler 单元测试
 */
@ExtendWith(MockitoExtension.class)
class CustomerHandlerTest {

    @Mock
    private CustomerGateway customerGateway;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @Spy
    private CustomerConvertor customerConvertor = new CustomerConvertor() {
        @Override
        public CustomerDTO toDTO(Customer customer) {
            if (customer == null) return null;
            CustomerDTO dto = new CustomerDTO();
            dto.setCustomerId(customer.getCustomerId());
            dto.setCustomerName(customer.getCustomerName());
            dto.setCompanyType(customer.getCompanyType() != null ? customer.getCompanyType().name() : null);
            return dto;
        }
    };

    @InjectMocks
    private CustomerHandler customerHandler;

    @Test
    void shouldCreateCustomerSuccessfully() {
        CustomerAddCmd cmd = new CustomerAddCmd();
        cmd.setCustomerName("测试客户");
        cmd.setCompanyType("PRIVATE");

        // Gateway的create方法内部会调用setCustomerId，此处模拟该行为
        // 由于setCustomerId是package-private，需要通过反射或调整测试策略
        doAnswer(invocation -> {
            // 实际由GatewayImpl中MyBatis-Plus insert后回填ID
            return null;
        }).when(customerGateway).create(any(Customer.class));

        Response response = customerHandler.add(cmd);

        assertTrue(response.isSuccess());
        verify(customerGateway).create(any(Customer.class));
        verify(domainEventPublisher).publish(any());
    }

    @Test
    void shouldReturnEmptyWhenNoCustomersFound() {
        when(customerGateway.listByName("不存在")).thenReturn(Collections.emptyList());

        CustomerListByNameQry qry = new CustomerListByNameQry();
        qry.setCustomerName("不存在");

        MultiResponse<CustomerDTO> response = customerHandler.listByName(qry);

        assertTrue(response.isSuccess());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void shouldReturnCustomersWhenFound() {
        // 使用rebuild构建带ID的Customer，避免调用package-private的setter
        Customer customer = Customer.rebuild(1L, "张三", CompanyType.PRIVATE);
        when(customerGateway.listByName("张三")).thenReturn(List.of(customer));

        CustomerListByNameQry qry = new CustomerListByNameQry();
        qry.setCustomerName("张三");

        MultiResponse<CustomerDTO> response = customerHandler.listByName(qry);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
        assertEquals("张三", response.getData().get(0).getCustomerName());
    }
}
