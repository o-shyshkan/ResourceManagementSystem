package com.test.api.mapper;

import com.test.api.model.Resource;
import com.test.api.model.dto.response.ResourceResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResponseMapper {

    ResourceResponseDto toDto(Resource resource);
}
