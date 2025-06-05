package com.test.model;

import lombok.Data;

@Data
public class Location {
    private Long id;
    private String streetAddress;
    private String city;
    private Integer postalCode;
    private String countryCode;
}
