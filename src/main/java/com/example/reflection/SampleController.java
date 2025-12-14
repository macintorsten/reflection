package com.example.reflection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/samples")
@Tag(name = "Samples", description = "Operations on Sample resources")
public class SampleController {

    @Autowired
    private SampleRepository repository;

        @PostMapping
        @Operation(operationId = "createSample", summary = "Create a sample", description = "Creates a new Sample entity")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sample created",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = SampleDTO.class),
                    examples = @ExampleObject(name = "SampleResponse", value = "{\n  \"text\": \"Hello World\",\n  \"number\": 42,\n  \"status\": \"active\",\n  \"extras\": { \"foo\": \"1\" }\n}"))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "ValidationError", value = "{\n  \"message\": \"Validation failed\",\n  \"status\": 400,\n  \"timestamp\": \"2025-12-13T15:00:00Z\",\n  \"fieldErrors\": [\n    { \"field\": \"text\", \"message\": \"size must be between 3 and 100\" },\n    { \"field\": \"number\", \"message\": \"must be less than or equal to 1000\" }\n  ]\n}")))
        })
        public ResponseEntity<SampleDTO> createSample(@Valid @org.springframework.web.bind.annotation.RequestBody SampleDTO dto) {
        Sample entity = new Sample();
        entity.text = dto.text;
        entity.number = dto.number;
        entity.status = dto.status;
        entity.mapField = dto.extras == null ? null : dto.extras.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Integer.valueOf(e.getValue())));
        entity.doubleArray = null; // not persisted
        Sample saved = repository.save(entity);
        SampleDTO response = new SampleDTO();
        response.text = saved.text;
        response.number = saved.number;
        response.status = saved.status;
        response.extras = dto.extras;
        return ResponseEntity.ok(response);
    }

    @GetMapping
        @Operation(operationId = "listSamples", summary = "List samples", description = "Returns all Sample entities")
        @ApiResponse(responseCode = "200", description = "List of samples",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SampleDTO.class),
            examples = @ExampleObject(name = "ListResponse", value = "[\n  { \n    \"text\": \"A\", \n    \"number\": 1, \n    \"status\": \"active\" \n  }\n]")))
    public List<SampleDTO> listSamples() {
        return repository.findAll().stream().map(entity -> {
            SampleDTO dto = new SampleDTO();
            dto.text = entity.text;
            dto.number = entity.number;
            dto.status = entity.status;
            dto.extras = entity.mapField == null ? null : entity.mapField.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
            return dto;
        }).collect(Collectors.toList());
    }
}
