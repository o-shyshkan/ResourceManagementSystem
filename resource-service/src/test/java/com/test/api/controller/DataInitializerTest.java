package com.test.api.controller;

import com.test.api.model.Characteristic;
import com.test.api.model.CharacteristicType;
import com.test.api.model.Resource;
import com.test.api.model.ResourceType;
import com.test.api.service.ResourcesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DataInitializerTest {
    @Mock
    private ResourcesService resourceService;

    @InjectMocks
    private DataInitializer dataInitializer;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dataInitializer).build();
    }

    @Test
    void inject_ShouldCallResourceServiceThreeTimes() throws Exception {
        mockMvc.perform(get("/inject"))
                .andExpect(status().isOk());
        verify(resourceService, times(3)).add(any(Resource.class));
    }

    @Test
    void inject_ShouldCreateCorrectResources() {
        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        dataInitializer.inject();
        verify(resourceService, times(3)).add(resourceCaptor.capture());
        List<Resource> capturedResources = resourceCaptor.getAllValues();
        assertEquals(3, capturedResources.size());
        Resource resource1 = capturedResources.get(0);
        assertEquals(ResourceType.CONNECTION_POINT, resource1.getResourceType());
        assertEquals("EE", resource1.getCountryCode());
        assertEquals("Rand tee 1", resource1.getLocation().getStreetAddress());
        assertEquals("Tallinn", resource1.getLocation().getCity());
        assertEquals(10001, resource1.getLocation().getPostalCode());
        assertEquals("EE", resource1.getLocation().getCountryCode());
        assertEquals(2, resource1.getCharacteristics().size());
        Resource resource2 = capturedResources.get(1);
        assertEquals(ResourceType.METERING_POINT, resource2.getResourceType());
        assertEquals("EE", resource2.getCountryCode());
        assertEquals("Pine tee 2", resource2.getLocation().getStreetAddress());
        assertEquals("Tartu", resource2.getLocation().getCity());
        assertEquals(20002, resource2.getLocation().getPostalCode());
        assertEquals("EE", resource2.getLocation().getCountryCode());
        assertEquals(2, resource2.getCharacteristics().size());
        Resource resource3 = capturedResources.get(2);
        assertEquals(ResourceType.CONNECTION_POINT, resource3.getResourceType());
        assertEquals("FI", resource3.getCountryCode());
        assertEquals("Silver tee 3", resource3.getLocation().getStreetAddress());
        assertEquals("Helsinki", resource3.getLocation().getCity());
        assertEquals(30003, resource3.getLocation().getPostalCode());
        assertEquals("FI", resource3.getLocation().getCountryCode());
        assertEquals(2, resource3.getCharacteristics().size());
    }

    @Test
    void inject_ShouldCreateCorrectCharacteristics() {
        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        dataInitializer.inject();
        verify(resourceService, times(3)).add(resourceCaptor.capture());
        List<Resource> capturedResources = resourceCaptor.getAllValues();
        List<Characteristic> characteristics1 = capturedResources.get(0).getCharacteristics();
        assertEquals("1", characteristics1.get(0).getCode());
        assertEquals(CharacteristicType.CONSUMPTION_TYPE, characteristics1.get(0).getCharacteristicType());
        assertEquals("220", characteristics1.get(0).getCharacteristicValue());
        assertEquals("2", characteristics1.get(1).getCode());
        assertEquals(CharacteristicType.CONSUMPTION_TYPE, characteristics1.get(1).getCharacteristicType());
        assertEquals("380", characteristics1.get(1).getCharacteristicValue());
        List<Characteristic> characteristics2 = capturedResources.get(1).getCharacteristics();
        assertEquals("3", characteristics2.get(0).getCode());
        assertEquals(CharacteristicType.CHARGING_POINT, characteristics2.get(0).getCharacteristicType());
        assertEquals("2 socket", characteristics2.get(0).getCharacteristicValue());
        assertEquals("4", characteristics2.get(1).getCode());
        assertEquals(CharacteristicType.CONSUMPTION_TYPE, characteristics2.get(1).getCharacteristicType());
        assertEquals("220", characteristics2.get(1).getCharacteristicValue());
        List<Characteristic> characteristics3 = capturedResources.get(2).getCharacteristics();
        assertEquals("5", characteristics3.get(0).getCode());
        assertEquals(CharacteristicType.CONNECTION_POINT_STATUS, characteristics3.get(0).getCharacteristicType());
        assertEquals("Available", characteristics3.get(0).getCharacteristicValue());
        assertEquals("6", characteristics3.get(1).getCode());
        assertEquals(CharacteristicType.CONSUMPTION_TYPE, characteristics3.get(1).getCharacteristicType());
        assertEquals("380", characteristics3.get(1).getCharacteristicValue());
    }

    @Test
    void inject_WhenServiceThrowsException_ShouldPropagateException() {
        doThrow(new RuntimeException("Service error")).when(resourceService).add(any(Resource.class));
        assertThrows(RuntimeException.class, () -> dataInitializer.inject());
    }

    @Test
    void inject_ShouldCreateResourcesWithNullIds() {
        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        dataInitializer.inject();
        verify(resourceService, times(3)).add(resourceCaptor.capture());
        List<Resource> capturedResources = resourceCaptor.getAllValues();
        capturedResources.forEach(resource -> {
            assertNull(resource.getId());
            assertNull(resource.getLocation().getId());
            resource.getCharacteristics().forEach(characteristic ->
                    assertNull(characteristic.getId()));
        });
    }

    @Test
    void inject_EndpointMapping_ShouldRespondToGetRequest() throws Exception {
        mockMvc.perform(get("/inject"))
                .andExpect(status().isOk());
    }

    @Test
    void constructor_ShouldInitializeResourceService() {
        ResourcesService mockService = mock(ResourcesService.class);
        DataInitializer initializer = new DataInitializer(mockService);
        assertNotNull(initializer);
        initializer.inject();
        verify(mockService, times(3)).add(any(Resource.class));
    }
}
