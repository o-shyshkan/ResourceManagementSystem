package com.test.api.service;

import com.test.api.model.Resource;
import com.test.api.model.dto.request.ResourceRequestDto;

public interface ResourcesService {
    Resource add(Resource user);

    Resource get(Long id);

    Resource update(Resource resource);

    Resource updatePartial(ResourceRequestDto resourceRequestDto, Long id);

    void remove(Long id);

    String sendAll();
}
