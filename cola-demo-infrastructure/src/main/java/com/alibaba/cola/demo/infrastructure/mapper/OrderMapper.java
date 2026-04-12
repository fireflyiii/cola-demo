package com.alibaba.cola.demo.infrastructure.mapper;

import com.alibaba.cola.demo.infrastructure.dataobject.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
}
