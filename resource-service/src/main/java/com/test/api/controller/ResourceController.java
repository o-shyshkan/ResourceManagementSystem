package com.test.api.controller;

import com.test.api.mapper.RequestMapper;
import com.test.api.mapper.ResponseMapper;
import com.test.api.model.Resource;
import com.test.api.model.dto.request.ResourceRequestDto;
import com.test.api.model.dto.response.ResourceResponseDto;
import com.test.api.service.ResourcesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@Validated
@Tag(name = "Resource", description = "Resource management APIs")
public class ResourceController {
    public static final String DATA = "data";
    private final RequestMapper resourceRequestMapper;
    private final ResponseMapper resourceResponseMapper;
    private final ResourcesService resourceService;

    @Operation(summary = "Add resource.",
            description = "This method add resource to application.",
    responses = {
        @ApiResponse(
                responseCode = "200",
                description = "Resource created successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(
                                name = "Success Response",
                                value = "{\"data\": [{\"id\": 1, \"name\": \"Example Resource\"}]}"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                name = "Validation Error",
                                value = "{\"error\": \"Validation failed\", \"details\": [\"Name is required\"]}"
                        )
                )
        )
    }
    )
    @PostMapping("/add")
    public Map<String, List<ResourceResponseDto>> add(
            @Parameter(description = "Resource data to be created", required = true)
            @RequestBody @Valid ResourceRequestDto resourceRequestDto) {
        Resource resource = resourceService.add(resourceRequestMapper.fromDto(resourceRequestDto));
        return Map.of(DATA, List.of(resourceResponseMapper.toDto(resource)));
    }

    @Operation(summary = "Get resource by id.",
            description = "This method get resource by id from database.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Resource found successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "Success Response",
                                            value = "{\"data\": [{\"id\": 1, \"name\": \"Example Resource\"}]}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Resource not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Not Found Error",
                                            value = "{\"error\": \"Resource not found\", \"message\": \"Resource with ID 1 does not exist\"}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public Map<String, List<ResourceResponseDto>>  get(
            @Parameter(description = "Resource ID", required = true, example = "1")
            @PathVariable Long id) {
        return Map.of(DATA, List.of(resourceResponseMapper.toDto(resourceService.get(id))));
    }

    @Operation(summary = "Partly update resource by id",
            description = "This method partly update resource by id.")
    @PatchMapping("/{id}")
    public Map<String, List<ResourceResponseDto>> updatePatch(
            @Parameter(description = "Resource ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Partial resource data to be updated", required = true)
            @RequestBody @Valid ResourceRequestDto resourceRequestDto) {
        return Map.of(DATA, List.of(resourceResponseMapper.toDto(
                resourceService.updatePartial(resourceRequestDto, id))));
    }

    @Operation(summary = "Update resource by id",
            description = "This method update resource by id.")
    @PutMapping("/{id}")
    public Map<String, List<ResourceResponseDto>> updatePut(
            @Parameter(description = "Resource ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Complete resource data for replacement", required = true)
            @RequestBody @Valid ResourceRequestDto resourceRequestDto) {
        Resource resource = resourceRequestMapper.fromDto(resourceRequestDto);
        resource.setId(id);
        return Map.of(DATA, List.of(resourceResponseMapper.toDto(resourceService.update(resource))));
    }

    @Operation(summary = "Delete resource by id",
            description = "This method delete resource by id.")
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "Resource ID to delete", required = true, example = "1")
            @PathVariable Long id) {
        resourceService.remove(id);
    }

    @Operation(summary = "Send all resources from application to kafka.",
            description = "This method send all resources from application to kafka.")
    @GetMapping("/sendAll")
    public String getAll() {
        return resourceService.sendAll();
    }
}
