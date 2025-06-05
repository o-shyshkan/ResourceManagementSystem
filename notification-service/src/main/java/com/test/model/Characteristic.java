package com.test.model;


import lombok.Data;

@Data
public class Characteristic {
    private Long id;
    private String code;
    private CharacteristicType characteristicType;
    private String CharacteristicValue;

}
