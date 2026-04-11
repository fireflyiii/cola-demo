package com.alibaba.cola.demo.app.service.impl;

import com.alibaba.cola.demo.client.api.IUserService;
import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.demo.domain.user.User;
import com.alibaba.cola.demo.domain.user.gateway.UserGateway;
import com.alibaba.cola.demo.app.convertor.UserConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserGateway userGateway;

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userGateway.findByUsername(username);
        List<String> roles = userGateway.findRoleCodesByUsername(username);
        return UserConvertor.toDTO(user, roles);
    }
}
