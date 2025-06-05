package com.test.model;

import lombok.Data;

import java.util.List;

@Data
public class Resource {
    private Long id;
    private ResourceType resourceType;
    private String countryCode;
    private Location location;
    private List<Characteristic> Characteristics;
}
