package com.test.api.model.dto.response;

import com.test.api.model.Characteristic;
import com.test.api.model.Location;
import com.test.api.model.ResourceType;
import lombok.Data;
import java.util.List;

@Data
public class ResourceResponseDto {
    private Long id;
    private ResourceType resourceType;
    private String countryCode;
    private Location location;
    private List<Characteristic> Characteristics;
}
