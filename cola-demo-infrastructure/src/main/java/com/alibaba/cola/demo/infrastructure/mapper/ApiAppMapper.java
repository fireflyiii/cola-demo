package com.alibaba.cola.demo.infrastructure.mapper;

import com.alibaba.cola.demo.infrastructure.dataobject.ApiAppEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * API应用Mapper
 */
@Mapper
public interface ApiAppMapper extends BaseMapper<ApiAppEntity> {
}
