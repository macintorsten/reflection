package com.example.reflection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
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

    private final SampleService service;

    public SampleController(SampleService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(operationId = "createSample", summary = "Create a sample", description = "Creates a new Sample entity")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sample created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SampleDTO.class),
                examples = @ExampleObject(name = "SampleResponse", value = "{\n  \"text\": \"Hello World\",\n  \"number\": 42,\n  \"status\": \"active\",\n  \"extras\": { \"foo\": \"1\" }\n}"))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "ValidationError", value = "{\n  \"message\": \"Validation failed\",\n  \"status\": 400,\n  \"timestamp\": \"2025-12-13T15:00:00Z\",\n  \"fieldErrors\": [\n    { \"field\": \"text\", \"message\": \"size must be between 3 and 100\" },\n    { \"field\": \"number\", \"message\": \"must be less than or equal to 1000\" }\n  ]\n}"))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SampleDTO> createSample(@Valid @org.springframework.web.bind.annotation.RequestBody SampleDTO dto) {
        SampleDTO response = service.createSample(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(operationId = "listSamples", summary = "List samples", description = "Returns all Sample entities")
    @ApiResponse(responseCode = "200", description = "List of samples",
        content = @Content(mediaType = "application/json", 
            schema = @Schema(type = "array", implementation = SampleDTO[].class),
            examples = @ExampleObject(name = "ListResponse", value = "[\n  { \n    \"text\": \"A\", \n    \"number\": 1, \n    \"status\": \"active\" \n  }\n]")))
    public List<SampleDTO> listSamples() {
        return service.listSamples();
    }
}
