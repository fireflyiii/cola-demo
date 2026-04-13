package com.alibaba.cola.demo.client.dto.data;

import com.alibaba.cola.dto.DTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 角色DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO extends DTO {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private Integer status;
}
