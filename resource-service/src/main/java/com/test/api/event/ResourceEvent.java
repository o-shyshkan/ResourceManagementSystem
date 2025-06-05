package com.test.api.event;

import com.test.api.model.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceEvent {
    private Resource resource;
}
