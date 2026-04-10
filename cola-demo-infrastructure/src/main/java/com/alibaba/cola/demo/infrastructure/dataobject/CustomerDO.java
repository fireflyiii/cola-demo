package com.alibaba.cola.demo.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("customer")
public class CustomerDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String customerName;
    private String companyType;
}