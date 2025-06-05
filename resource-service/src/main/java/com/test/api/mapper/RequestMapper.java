package com.test.api.mapper;

import com.test.api.model.Resource;
import com.test.api.model.dto.request.ResourceRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RequestMapper {
    Resource fromDto(ResourceRequestDto dto);

    void updateResourcesFromDto(ResourceRequestDto dto, @MappingTarget Resource resource);
}

