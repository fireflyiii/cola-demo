package com.alibaba.cola.demo.client.dto;

import com.alibaba.cola.demo.client.dto.data.UserDTO;
import com.alibaba.cola.dto.SingleResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 登录响应
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginResponse extends SingleResponse<LoginData> {

    private UserDTO user;

    public LoginResponse(LoginData data, UserDTO user) {
        this.setSuccess(true);
        this.setData(data);
        this.user = user;
    }
}
