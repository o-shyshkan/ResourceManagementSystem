package com.test.api.service.impl;

import com.test.api.event.ResourceEvent;
import com.test.api.mapper.RequestMapper;
import com.test.api.model.Resource;
import com.test.api.model.dto.request.ResourceRequestDto;
import com.test.api.repository.ResourceRepository;
import com.test.api.service.ResourcesService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourcesServiceImpl implements ResourcesService {
    public static final String NOTIFICATION_TOPIC = "notificationTopic";
    public static final String NOTIFICATION_TOPIC_ALL_DATA = "notificationTopicAllData";
    private final RequestMapper resourceRequestMapper;
    private final ResourceRepository resourceRepository;
    private final KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    @Override
    public Resource add(Resource resource) {
        Resource resourceSaved = resourceRepository.save(resource);
        kafkaTemplate.send(NOTIFICATION_TOPIC, new ResourceEvent(resourceSaved));
        return resourceSaved;
    }

    @Override
    public Resource get(Long id) {
        return resourceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Resource with id " + id + " not found"));
    }

    @Override
    public void remove(Long id) {
        resourceRepository.deleteById(id);
    }

    @Override
    public Resource update(Resource resource) {
        Resource resourceUpdated = resourceRepository.save(resource);
        kafkaTemplate.send(NOTIFICATION_TOPIC, new ResourceEvent(resourceUpdated));
        return resourceUpdated;
    }

    @Override
    public Resource updatePartial(ResourceRequestDto resourceRequestDto, Long id) {
        Resource resource = resourceRepository.getReferenceById(id);
        resourceRequestMapper.updateResourcesFromDto(resourceRequestDto, resource);
        return resourceRepository.save(resource);
    }

    @Override
    public String sendAll() {
        List<Resource> resources = resourceRepository.findAll();
        resources.forEach(e->kafkaTemplate.send(NOTIFICATION_TOPIC_ALL_DATA, new ResourceEvent(e)));
        return String.format("Send %s items from Resource.", resources.size());
    }
}
