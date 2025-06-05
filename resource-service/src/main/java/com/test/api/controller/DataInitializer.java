package com.test.api.controller;

import com.test.api.model.Characteristic;
import com.test.api.model.CharacteristicType;
import com.test.api.model.Location;
import com.test.api.model.Resource;
import com.test.api.model.ResourceType;
import com.test.api.service.ResourcesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/inject")
@Tag(name = "DataInitializer", description = "Init data controller.")
public class DataInitializer {
    private final ResourcesService resourceService;

    public DataInitializer(ResourcesService resourceService) {
        this.resourceService = resourceService;
    }

    @Operation(summary = "Add init data.",
            description = "This method add init data to application.")
    @GetMapping
    public void inject() {
        Location location1 = new Location(null, "Rand tee 1", "Tallinn",
                10001,"EE");
        Characteristic characteristic1 = new Characteristic(null, "1",
                CharacteristicType.CONSUMPTION_TYPE, "220");
        Characteristic characteristic12 = new Characteristic(null, "2",
                CharacteristicType.CONSUMPTION_TYPE, "380");
        Resource resource1 = new Resource(null, ResourceType.CONNECTION_POINT, "EE",
                location1, List.of(characteristic1, characteristic12));
        resourceService.add(resource1);
        Location location2 = new Location(null, "Pine tee 2", "Tartu",
                20002,"EE");
        Characteristic characteristic21 = new Characteristic(null, "3",
                CharacteristicType.CHARGING_POINT, "2 socket");
        Characteristic characteristic22 = new Characteristic(null, "4",
                CharacteristicType.CONSUMPTION_TYPE, "220");
        Resource resource2 = new Resource(null, ResourceType.METERING_POINT, "EE",
                location2, List.of(characteristic21, characteristic22));
        resourceService.add(resource2);
        Location location3 = new Location(null, "Silver tee 3", "Helsinki",
                30003,"FI");
        Characteristic characteristic31 = new Characteristic(null, "5",
                CharacteristicType.CONNECTION_POINT_STATUS, "Available");
        Characteristic characteristic32 = new Characteristic(null, "6",
                CharacteristicType.CONSUMPTION_TYPE, "380");
        Resource resource3 = new Resource(null, ResourceType.CONNECTION_POINT, "FI",
                location3, List.of(characteristic31, characteristic32));
        resourceService.add(resource3);
    }
}
