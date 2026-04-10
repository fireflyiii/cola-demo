package com.alibaba.cola.demo.adapter.web;

import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    private final UserGateway userGateway;

    public TestController(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @GetMapping("/admin-password")
    public Map<String, Object> getAdminPassword() {
        User user = userGateway.findByUsername("admin");
        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            result.put("username", user.getUsername());
            result.put("password_in_db", user.getPassword());
        } else {
            result.put("error", "User not found");
        }
        return result;
    }
}
