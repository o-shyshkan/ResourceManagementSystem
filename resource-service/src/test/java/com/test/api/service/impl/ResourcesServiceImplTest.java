package com.test.api.service.impl;

import com.test.api.event.ResourceEvent;
import com.test.api.mapper.RequestMapper;
import com.test.api.model.Characteristic;
import com.test.api.model.CharacteristicType;
import com.test.api.model.Location;
import com.test.api.model.Resource;
import com.test.api.model.ResourceType;
import com.test.api.model.dto.request.ResourceRequestDto;
import com.test.api.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ResourcesServiceImpl.class)
public class ResourcesServiceImplTest {
    public static final String RAND_TEE_1 = "Rand tee 1";
    public static final String TALLINN = "Tallinn";
    public static final String EE = "EE";
    public static final String NUMBER_1 = "1";
    public static final String NUMBER_220 = "220";
    public static final String NOTIFICATION_TOPIC = "notificationTopic";
    public static final String RESOURCE_WITH_ID_100_NOT_FOUND = "Resource with id 100 not found";
    public static final String UA = "UA";
    public static final String NUMBER_2 = "2";
    public static final String FAST = "Fast";
    public static final String JOHI_TEE = "123 Johi tee";
    public static final String PADRI_TEE = "123 Padri tee";
    public static final String US = "US";
    public static final String RAND_TEE = "123 Rand tee";
    public static final String NUMBER_3 = "3";
    public static final String STANDARD = "Standard";
    public static final String ACTIVE = "Active";
    public static final String SEND_3_ITEMS_FROM_RESOURCE = "Send 3 items from Resource.";
    public static final String NOTIFICATION_TOPIC_ALL_DATA = "notificationTopicAllData";
    public static final String PINE_10_TEE = "Pine 10 tee";
    public static final String TARTU = "Tartu";
    public static final String DE = "DE";
    public static final String CO_1 = "CO1";
    public static final String CH_1 = "CH1";
    public static final String ST_1 = "ST1";
    public static final String ONLINE = "Online";
    public static final String ULTRA_FAST = "Ultra Fast";
    public static final String HIGH = "High";
    public static final String KELA = "Kela";
    public static final String SEND_0_ITEMS_FROM_RESOURCE = "Send 0 items from Resource.";
    @MockitoBean
    private RequestMapper resourceRequestMapper;

    @MockitoBean
    private ResourceRepository resourceRepository;

    @MockitoBean
    private KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    @Autowired
    private ResourcesServiceImpl resourcesService;

    private Resource testResource;
    private Location testLocation;
    private Characteristic testCharacteristic;
    private ResourceRequestDto testResourceRequestDto;

    @BeforeEach
    void setUp() {
        testLocation = new Location();
        testLocation.setId(1L);
        testLocation.setStreetAddress(RAND_TEE_1);
        testLocation.setCity(TALLINN);
        testLocation.setPostalCode(10001);
        testLocation.setCountryCode(EE);
        Characteristic testCharacteristic = new Characteristic();
        testCharacteristic.setId(1L);
        testCharacteristic.setCode(NUMBER_1);
        testCharacteristic.setCharacteristicType(CharacteristicType.CONSUMPTION_TYPE);
        testCharacteristic.setCharacteristicValue(NUMBER_220);
        List<Characteristic> characteristics = List.of(testCharacteristic);
        testResource = new Resource();
        testResource.setId(1L);
        testResource.setResourceType(ResourceType.METERING_POINT);
        testResource.setCountryCode(EE);
        testResource.setLocation(testLocation);
        testResource.setCharacteristics(Arrays.asList(testCharacteristic));
        testResourceRequestDto = new ResourceRequestDto();
        testResourceRequestDto.setResourceType(ResourceType.METERING_POINT);
        testResourceRequestDto.setCountryCode(EE);
        testResourceRequestDto.setLocation(testLocation);
        testResourceRequestDto.setCharacteristics(List.of(testCharacteristic));
    }
    @Test
    void add_ShouldSaveResourceAndSendKafkaMessage() {
        Resource savedResource = new Resource();
        savedResource.setId(2L);
        savedResource.setResourceType(ResourceType.CONNECTION_POINT);
        savedResource.setCountryCode(EE);
        savedResource.setLocation(testLocation);
        savedResource.setCharacteristics(Arrays.asList(testCharacteristic));
        when(resourceRepository.save(testResource)).thenReturn(savedResource);
        Resource result = resourcesService.add(testResource);
        assertNotNull(result);
        assertEquals(savedResource.getId(), result.getId());
        assertEquals(savedResource.getResourceType(), result.getResourceType());
        assertEquals(savedResource.getCountryCode(), result.getCountryCode());
        assertEquals(savedResource.getLocation().getCity(), result.getLocation().getCity());
        assertEquals(1, result.getCharacteristics().size());
        verify(resourceRepository, times(1)).save(testResource);
        verify(kafkaTemplate, times(1)).send(eq(NOTIFICATION_TOPIC), any(ResourceEvent.class));
    }

    @Test
    void get_WithValidId_ShouldReturnResource() {
        Long resourceId = 1L;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(testResource));
        Resource result = resourcesService.get(resourceId);
        assertNotNull(result);
        assertEquals(testResource.getId(), result.getId());
        assertEquals(testResource.getResourceType(), result.getResourceType());
        assertEquals(testResource.getCountryCode(), result.getCountryCode());
        assertNotNull(result.getLocation());
        assertEquals(TALLINN, result.getLocation().getCity());
        assertEquals(1, result.getCharacteristics().size());
        assertEquals(NUMBER_1, result.getCharacteristics().get(0).getCode());
        verify(resourceRepository, times(1)).findById(resourceId);
    }

    @Test
    void get_WithInvalidId_ShouldThrowEntityNotFoundException() {
        Long invalidId = 100L;
        when(resourceRepository.findById(invalidId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> resourcesService.get(invalidId)
        );
        assertEquals(RESOURCE_WITH_ID_100_NOT_FOUND, exception.getMessage());
        verify(resourceRepository, times(1)).findById(invalidId);
    }

    @Test
    void remove_ShouldCallDeleteById() {
        Long resourceId = 1L;
        resourcesService.remove(resourceId);
        verify(resourceRepository, times(1)).deleteById(resourceId);
    }

    @Test
    void update_ShouldUpdateResourceAndSendKafkaMessage() {
        Resource updatedResource = new Resource();
        updatedResource.setId(1L);
        updatedResource.setResourceType(ResourceType.CONNECTION_POINT);
        updatedResource.setCountryCode(UA);
        updatedResource.setLocation(testLocation);
        Characteristic updatedCharacteristic = new Characteristic(2L, NUMBER_2, CharacteristicType.CHARGING_POINT, FAST);
        updatedResource.setCharacteristics(Arrays.asList(updatedCharacteristic));
        when(resourceRepository.save(testResource)).thenReturn(updatedResource);
        Resource result = resourcesService.update(testResource);
        assertNotNull(result);
        assertEquals(updatedResource.getId(), result.getId());
        assertEquals(ResourceType.CONNECTION_POINT, result.getResourceType());
        assertEquals(UA, result.getCountryCode());
        assertEquals(NUMBER_2, result.getCharacteristics().get(0).getCode());
        verify(resourceRepository, times(1)).save(testResource);
        verify(kafkaTemplate, times(1)).send(eq(NOTIFICATION_TOPIC), any(ResourceEvent.class));
    }

    @Test
    void updatePartial_ShouldUpdateResourceFromDto() {
        Long resourceId = 1L;
        Resource existingResource = new Resource();
        existingResource.setId(resourceId);
        existingResource.setResourceType(ResourceType.METERING_POINT);
        existingResource.setCountryCode(EE);
        existingResource.setLocation(testLocation);
        existingResource.setCharacteristics(Arrays.asList(testCharacteristic));
        Resource updatedResource = new Resource();
        updatedResource.setId(resourceId);
        updatedResource.setResourceType(ResourceType.CONNECTION_POINT);
        updatedResource.setCountryCode(UA);
        updatedResource.setLocation(testLocation);
        updatedResource.setCharacteristics(Arrays.asList(testCharacteristic));
        when(resourceRepository.getReferenceById(resourceId)).thenReturn(existingResource);
        when(resourceRepository.save(existingResource)).thenReturn(updatedResource);
        Resource result = resourcesService.updatePartial(testResourceRequestDto, resourceId);
        assertNotNull(result);
        assertEquals(updatedResource.getId(), result.getId());
        assertEquals(ResourceType.CONNECTION_POINT, result.getResourceType());
        assertEquals(UA, result.getCountryCode());
        verify(resourceRepository, times(1)).getReferenceById(resourceId);
        verify(resourceRequestMapper, times(1)).updateResourcesFromDto(testResourceRequestDto, existingResource);
        verify(resourceRepository, times(1)).save(existingResource);
    }

    @Test
    void sendAll_WithEmptyList_ShouldReturnZeroMessage() {
        List<Resource> emptyList = Arrays.asList();
        when(resourceRepository.findAll()).thenReturn(emptyList);
        String result = resourcesService.sendAll();
        assertEquals(SEND_0_ITEMS_FROM_RESOURCE, result);
        verify(resourceRepository, times(1)).findAll();
        verify(kafkaTemplate, never()).send(anyString(), any(ResourceEvent.class));
    }

    @Test
    void sendAll_WithMultipleResources_ShouldSendAllToKafkaAndReturnCount() {
        Location location1 = new Location(1L, JOHI_TEE, TALLINN, 10001, US);
        Location location2 = new Location(2L, PADRI_TEE, TARTU, 02101, US);
        Location location3 = new Location(3L, RAND_TEE, KELA, 60601, US);
        Characteristic char1 = new Characteristic(1L, NUMBER_1, CharacteristicType.CONSUMPTION_TYPE, STANDARD);
        Characteristic char2 = new Characteristic(2L, NUMBER_2, CharacteristicType.CHARGING_POINT, FAST);
        Characteristic char3 = new Characteristic(3L, NUMBER_3, CharacteristicType.CONNECTION_POINT_STATUS, ACTIVE);
        Resource resource1 = new Resource();
        resource1.setId(1L);
        resource1.setResourceType(ResourceType.METERING_POINT);
        resource1.setCountryCode(EE);
        resource1.setLocation(location1);
        resource1.setCharacteristics(Arrays.asList(char1));
        Resource resource2 = new Resource();
        resource2.setId(2L);
        resource2.setResourceType(ResourceType.CONNECTION_POINT);
        resource2.setCountryCode(EE);
        resource2.setLocation(location2);
        resource2.setCharacteristics(Arrays.asList(char2));
        Resource resource3 = new Resource();
        resource3.setId(3L);
        resource3.setResourceType(ResourceType.METERING_POINT);
        resource3.setCountryCode(UA);
        resource3.setLocation(location3);
        resource3.setCharacteristics(Arrays.asList(char3));
        List<Resource> resources = Arrays.asList(resource1, resource2, resource3);
        when(resourceRepository.findAll()).thenReturn(resources);
        String result = resourcesService.sendAll();
        assertEquals(SEND_3_ITEMS_FROM_RESOURCE, result);
        verify(resourceRepository, times(1)).findAll();
        verify(kafkaTemplate, times(3)).send(eq(NOTIFICATION_TOPIC_ALL_DATA), any(ResourceEvent.class));
    }

    @Test
    void sendAll_WithSingleResource_ShouldSendToKafkaAndReturnCount() {
        List<Resource> singleResourceList = Arrays.asList(testResource);
        when(resourceRepository.findAll()).thenReturn(singleResourceList);
        String result = resourcesService.sendAll();
        assertEquals("Send 1 items from Resource.", result);
        verify(resourceRepository, times(1)).findAll();
        verify(kafkaTemplate, times(1)).send(eq(NOTIFICATION_TOPIC_ALL_DATA), any(ResourceEvent.class));
    }

    @Test
    void add_ShouldCreateCorrectResourceEvent() {
        Resource savedResource = new Resource();
        savedResource.setId(2L);
        savedResource.setResourceType(ResourceType.CONNECTION_POINT);
        savedResource.setCountryCode(US);
        savedResource.setLocation(testLocation);
        savedResource.setCharacteristics(Arrays.asList(testCharacteristic));
        when(resourceRepository.save(testResource)).thenReturn(savedResource);
        resourcesService.add(testResource);
        verify(kafkaTemplate).send(eq(NOTIFICATION_TOPIC), argThat(event ->
                event != null && event.getResource().equals(savedResource)
        ));
    }

    @Test
    void update_ShouldCreateCorrectResourceEvent() {
        Resource updatedResource = new Resource();
        updatedResource.setId(1L);
        updatedResource.setResourceType(ResourceType.CONNECTION_POINT);
        updatedResource.setCountryCode(EE);
        updatedResource.setLocation(testLocation);
        updatedResource.setCharacteristics(Arrays.asList(testCharacteristic));
        when(resourceRepository.save(testResource)).thenReturn(updatedResource);
        resourcesService.update(testResource);
        verify(kafkaTemplate).send(eq(NOTIFICATION_TOPIC), argThat(event ->
                event != null && event.getResource().equals(updatedResource)
        ));
    }

    @Test
    void add_WithComplexResourceStructure_ShouldHandleRelationships() {
        Location complexLocation = new Location(10L, PINE_10_TEE, TARTU, 12345, DE);
        Characteristic consumption = new Characteristic(10L, CO_1, CharacteristicType.CONSUMPTION_TYPE, HIGH);
        Characteristic charging = new Characteristic(11L, CH_1, CharacteristicType.CHARGING_POINT, ULTRA_FAST);
        Characteristic status = new Characteristic(12L, ST_1, CharacteristicType.CONNECTION_POINT_STATUS, ONLINE);
        Resource complexResource = new Resource();
        complexResource.setResourceType(ResourceType.CONNECTION_POINT);
        complexResource.setCountryCode(DE);
        complexResource.setLocation(complexLocation);
        complexResource.setCharacteristics(Arrays.asList(consumption, charging, status));
        Resource savedComplexResource = new Resource();
        savedComplexResource.setId(100L);
        savedComplexResource.setResourceType(ResourceType.CONNECTION_POINT);
        savedComplexResource.setCountryCode(DE);
        savedComplexResource.setLocation(complexLocation);
        savedComplexResource.setCharacteristics(Arrays.asList(consumption, charging, status));
        when(resourceRepository.save(complexResource)).thenReturn(savedComplexResource);
        Resource result = resourcesService.add(complexResource);
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(ResourceType.CONNECTION_POINT, result.getResourceType());
        assertEquals(DE, result.getCountryCode());
        assertEquals(PINE_10_TEE, result.getLocation().getStreetAddress());
        assertEquals(TARTU, result.getLocation().getCity());
        assertEquals(12345, result.getLocation().getPostalCode());
        assertEquals(3, result.getCharacteristics().size());
        assertTrue(result.getCharacteristics().stream()
                .anyMatch(ch -> ch.getCharacteristicType() == CharacteristicType.CONSUMPTION_TYPE));
        assertTrue(result.getCharacteristics().stream()
                .anyMatch(ch -> ch.getCharacteristicType() == CharacteristicType.CHARGING_POINT));
        assertTrue(result.getCharacteristics().stream()
                .anyMatch(ch -> ch.getCharacteristicType() == CharacteristicType.CONNECTION_POINT_STATUS));
        verify(resourceRepository, times(1)).save(complexResource);
        verify(kafkaTemplate, times(1)).send(eq(NOTIFICATION_TOPIC), any(ResourceEvent.class));
    }
}
