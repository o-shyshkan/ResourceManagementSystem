package com.test.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.api.mapper.RequestMapper;
import com.test.api.mapper.ResponseMapper;
import com.test.api.model.Characteristic;
import com.test.api.model.CharacteristicType;
import com.test.api.model.Location;
import com.test.api.model.Resource;
import com.test.api.model.ResourceType;
import com.test.api.model.dto.request.ResourceRequestDto;
import com.test.api.model.dto.response.ResourceResponseDto;
import com.test.api.service.ResourcesService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
@ExtendWith(MockitoExtension.class)
public class ResourceControllerTests {

    public static final String URL_RESOURCES_ADD = "/resources/add";
    public static final String RAND_TEE_1 = "Rand tee 1";
    public static final String TALLINN = "Tallinn";
    public static final String EE = "EE";
    public static final String NUMBER_1 = "1";
    public static final String NUMBER_220 = "220";
    public static final String NUMBER_2 = "2";
    public static final String FAST = "FAST";
    public static final String METERING_POINT = "METERING_POINT";
    public static final String CONSUMPTION_TYPE = "CONSUMPTION_TYPE";
    public static final String CHARGING_POINT = "CHARGING_POINT";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String SEND_10_ITEMS_FROM_RESOURCE = "Send 10 items from Resource.";
    public static final String SERVICE_ERROR = "Service error";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private RequestMapper resourceRequestMapper;
    @MockitoBean
    private ResponseMapper resourceResponseMapper;
    @MockitoBean
    private ResourcesService resourceService;
    private ResourceRequestDto resourceRequestDto;
    private ResourceResponseDto resourceResponseDto;
    private Resource resource;
    private Location location;
    private List<Characteristic> characteristics;

    @BeforeEach
    public void setup(){
        location = new Location();
        location.setId(1L);
        location.setStreetAddress(RAND_TEE_1);
        location.setCity(TALLINN);
        location.setPostalCode(10001);
        location.setCountryCode(EE);
        Characteristic characteristic1 = new Characteristic();
        characteristic1.setId(1L);
        characteristic1.setCode(NUMBER_1);
        characteristic1.setCharacteristicType(CharacteristicType.CONSUMPTION_TYPE);
        characteristic1.setCharacteristicValue(NUMBER_220);
        Characteristic characteristic2 = new Characteristic();
        characteristic2.setId(2L);
        characteristic2.setCode(NUMBER_2);
        characteristic2.setCharacteristicType(CharacteristicType.CHARGING_POINT);
        characteristic2.setCharacteristicValue(FAST);
        List<Characteristic> characteristics = List.of(characteristic1, characteristic2);
        resourceRequestDto = new ResourceRequestDto();
        resourceRequestDto.setResourceType(ResourceType.METERING_POINT);
        resourceRequestDto.setCountryCode(EE);
        resourceRequestDto.setLocation(location);
        resourceRequestDto.setCharacteristics(List.of(characteristic1, characteristic2));
        resourceResponseDto = new ResourceResponseDto();
        resourceResponseDto.setId(1L);
        resourceResponseDto.setResourceType(ResourceType.METERING_POINT);
        resourceResponseDto.setCountryCode(EE);
        resourceResponseDto.setLocation(location);
        resourceResponseDto.setCharacteristics(characteristics);
        resource = new Resource();
        resource.setId(1L);
        resource.setResourceType(ResourceType.METERING_POINT);
        resource.setCountryCode(EE);
        resource.setLocation(location);
        resource.setCharacteristics(characteristics);
    }

    @Test
    void addResource_ShouldReturnCreatedResource_WhenValidInput() throws Exception {
        when(resourceRequestMapper.fromDto(any(ResourceRequestDto.class))).thenReturn(resource);
        when(resourceService.add(any(Resource.class))).thenReturn(resource);
        when(resourceResponseMapper.toDto(any(Resource.class))).thenReturn(resourceResponseDto);
        mockMvc.perform(post(URL_RESOURCES_ADD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resourceRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].resourceType").value(METERING_POINT))
                .andExpect(jsonPath("$.data[0].countryCode").value(EE))
                .andExpect(jsonPath("$.data[0].location.id").value(1L))
                .andExpect(jsonPath("$.data[0].location.streetAddress").value(RAND_TEE_1))
                .andExpect(jsonPath("$.data[0].location.city").value(TALLINN))
                .andExpect(jsonPath("$.data[0].location.postalCode").value(10001))
                .andExpect(jsonPath("$.data[0].location.countryCode").value(EE))
                .andExpect(jsonPath("$.data[0].characteristics[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].characteristics[0].code").value(1))
                .andExpect(jsonPath("$.data[0].characteristics[0].characteristicType").value(CONSUMPTION_TYPE))
                .andExpect(jsonPath("$.data[0].characteristics[0].characteristicValue").value(NUMBER_220))
                .andExpect(jsonPath("$.data[0].characteristics[1].id").value(2L))
                .andExpect(jsonPath("$.data[0].characteristics[1].code").value(2))
                .andExpect(jsonPath("$.data[0].characteristics[1].characteristicType").value(CHARGING_POINT))
                .andExpect(jsonPath("$.data[0].characteristics[1].characteristicValue").value(FAST));
        verify(resourceRequestMapper).fromDto(any(ResourceRequestDto.class));
        verify(resourceService).add(any(Resource.class));
        verify(resourceResponseMapper).toDto(any(Resource.class));
    }

    @Test
    void addResource_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        ResourceRequestDto invalidDto = new ResourceRequestDto();
        mockMvc.perform(post(URL_RESOURCES_ADD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
        verify(resourceService, never()).add(any(Resource.class));
    }

    @Test
    void getResource_ShouldReturnResource_WhenResourceExists() throws Exception {
        Long resourceId = 1L;
        when(resourceService.get(resourceId)).thenReturn(resource);
        when(resourceResponseMapper.toDto(resource)).thenReturn(resourceResponseDto);
        mockMvc.perform(get("/resources/{id}", resourceId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(resourceId))
                .andExpect(jsonPath("$.data[0].resourceType").value(METERING_POINT))
                .andExpect(jsonPath("$.data[0].countryCode").value(EE))
                .andExpect(jsonPath("$.data[0].location.id").value(1L))
                .andExpect(jsonPath("$.data[0].location.streetAddress").value(RAND_TEE_1))
                .andExpect(jsonPath("$.data[0].location.city").value(TALLINN))
                .andExpect(jsonPath("$.data[0].location.postalCode").value(10001))
                .andExpect(jsonPath("$.data[0].location.countryCode").value(EE))
                .andExpect(jsonPath("$.data[0].characteristics[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].characteristics[0].code").value(1))
                .andExpect(jsonPath("$.data[0].characteristics[0].characteristicType").value(CONSUMPTION_TYPE))
                .andExpect(jsonPath("$.data[0].characteristics[0].characteristicValue").value(NUMBER_220))
                .andExpect(jsonPath("$.data[0].characteristics[1].id").value(2L))
                .andExpect(jsonPath("$.data[0].characteristics[1].code").value(2))
                .andExpect(jsonPath("$.data[0].characteristics[1].characteristicType").value(CHARGING_POINT))
                .andExpect(jsonPath("$.data[0].characteristics[1].characteristicValue").value(FAST));
        verify(resourceService).get(resourceId);
        verify(resourceResponseMapper).toDto(resource);
    }

    @Test
    void getResource_ShouldReturnNotFound_WhenResourceDoesNotExist() throws Exception {
        Long resourceId = 100L;
        when(resourceService.get(resourceId)).thenThrow(new EntityNotFoundException(RESOURCE_NOT_FOUND));
        mockMvc.perform(get("/resources/{id}", resourceId))
                .andExpect(status().isNotFound());
        verify(resourceService).get(resourceId);
    }

    @Test
    void updatePutResource_ShouldReturnUpdatedResource_WhenValidInput() throws Exception {
        Long resourceId = 1L;
        Resource mappedResource = new Resource();
        mappedResource.setResourceType(ResourceType.CONNECTION_POINT);
        mappedResource.setCountryCode("UA");
        when(resourceRequestMapper.fromDto(any(ResourceRequestDto.class))).thenReturn(mappedResource);
        when(resourceService.update(any(Resource.class))).thenReturn(resource);
        when(resourceResponseMapper.toDto(resource)).thenReturn(resourceResponseDto);
        mockMvc.perform(put("/resources/{id}", resourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resourceRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].resourceType").value(METERING_POINT))
                .andExpect(jsonPath("$.data[0].countryCode").value(EE));
        verify(resourceRequestMapper).fromDto(any(ResourceRequestDto.class));
        verify(resourceService).update(any(Resource.class));
        verify(resourceResponseMapper).toDto(resource);
    }

    @Test
    void updatePatchResource_ShouldReturnUpdatedPartialResource_WhenValidInput() throws Exception {
        Long resourceId = 1L;
        when(resourceService.updatePartial(any(ResourceRequestDto.class), eq(resourceId)))
                .thenReturn(resource);
        when(resourceResponseMapper.toDto(resource)).thenReturn(resourceResponseDto);
        mockMvc.perform(patch("/resources/{id}", resourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resourceRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(resourceId))
                .andExpect(jsonPath("$.data[0].resourceType").value(METERING_POINT))
                .andExpect(jsonPath("$.data[0].countryCode").value(EE));
        verify(resourceService).updatePartial(any(ResourceRequestDto.class), eq(resourceId));
        verify(resourceResponseMapper).toDto(resource);
    }

    @Test
    void deleteResource_ShouldReturnNoContent_WhenResourceExists() throws Exception {
        Long resourceId = 1L;
        doNothing().when(resourceService).remove(resourceId);
        mockMvc.perform(delete("/resources/{id}", resourceId))
                .andExpect(status().isOk());
        verify(resourceService).remove(resourceId);
    }

    @Test
    void sendAll_ShouldReturnAllResources() throws Exception {
        String expectedResponse = SEND_10_ITEMS_FROM_RESOURCE;
        when(resourceService.sendAll()).thenReturn(expectedResponse);
        mockMvc.perform(get("/resources/sendAll"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
        verify(resourceService).sendAll();
    }

    @Test
    void addResource_ShouldHandleServiceException() throws Exception {
        when(resourceRequestMapper.fromDto(any(ResourceRequestDto.class))).thenReturn(resource);
        when(resourceService.add(any(Resource.class))).thenThrow(new RuntimeException(SERVICE_ERROR));
        mockMvc.perform(post("/resources/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resourceRequestDto)))
                .andExpect(status().isInternalServerError());
        verify(resourceService).add(any(Resource.class));
    }

    @Test
    void updatePatchResource_ShouldHandleInvalidId() throws Exception {
        String invalidId = "invalid";
        mockMvc.perform(patch("/resources/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resourceRequestDto)))
                .andExpect(status().isBadRequest());
        verify(resourceService, never()).updatePartial(any(), any());
    }
}
