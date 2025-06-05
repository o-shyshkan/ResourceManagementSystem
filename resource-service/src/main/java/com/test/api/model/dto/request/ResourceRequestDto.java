package com.test.api.model.dto.request;

import com.test.api.model.Characteristic;
import com.test.api.model.Location;
import com.test.api.model.ResourceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ResourceRequestDto {
    @NotNull(message = "ResourceType cannot be null")
    private ResourceType resourceType;
    private String countryCode;
    private Location location;
    private List<Characteristic> Characteristics;
}
