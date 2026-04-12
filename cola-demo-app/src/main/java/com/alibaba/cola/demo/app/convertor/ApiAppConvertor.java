package com.alibaba.cola.demo.app.convertor;

import com.alibaba.cola.demo.client.dto.data.ApiAppDTO;
import com.alibaba.cola.demo.domain.apiapp.ApiApp;
import org.mapstruct.Mapper;

/**
 * API应用DTO转换器
 */
@Mapper(componentModel = "spring")
public interface ApiAppConvertor {

    ApiAppDTO toDTO(ApiApp apiApp);
}
