package com.example.reflection.web.controller.v1;

import com.example.reflection.domain.model.Sample;
import com.example.reflection.service.SampleService;
import com.example.reflection.web.dto.v1.request.CreateSampleRequest;
import com.example.reflection.web.dto.v1.response.SampleResponse;
import com.example.reflection.web.exception.ErrorResponse;
import com.example.reflection.web.mapper.SampleMapperV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Sample operations - Version 1.
 * Handles HTTP requests and responses for /api/v1/samples endpoints.
 * Works with DTOs and delegates to service layer for business logic.
 */
@RestController
@RequestMapping("/api/v1/samples")
@Tag(name = "Samples V1", description = "Version 1 of Sample operations")
public class SampleControllerV1 {
    
    private static final Logger log = LoggerFactory.getLogger(SampleControllerV1.class);
    
    private final SampleService sampleService;
    private final SampleMapperV1 mapper;
    
    public SampleControllerV1(SampleService sampleService, SampleMapperV1 mapper) {
        this.sampleService = sampleService;
        this.mapper = mapper;
    }
    
    /**
     * Create a new sample.
     * 
     * @param request the request DTO with sample data
     * @return the created sample as response DTO
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a sample", description = "Creates a new Sample entity")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Sample created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SampleResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<SampleResponse> createSample(
            @Valid @RequestBody CreateSampleRequest request) {
        log.info("Received request to create sample: {}", request);
        
        // Request DTO → Domain
        Sample domain = mapper.toDomain(request);
        
        // Service processes domain object
        Sample created = sampleService.createSample(domain);
        
        // Domain → Response DTO
        SampleResponse response = mapper.toResponse(created);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * List all samples.
     * 
     * @return list of all samples as response DTOs
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List samples", description = "Returns all Sample entities")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = SampleResponse.class)))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<List<SampleResponse>> listSamples() {
        log.info("Received request to list all samples");
        
        List<Sample> samples = sampleService.listSamples();
        List<SampleResponse> responses = samples.stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get a sample by ID.
     * 
     * @param id the sample ID
     * @return the sample as response DTO
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get sample by ID", description = "Returns a sample by its ID")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Sample found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SampleResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Sample not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<SampleResponse> getSample(@PathVariable Long id) {
        log.info("Received request to get sample with ID: {}", id);
        
        Sample sample = sampleService.getSampleById(id);
        SampleResponse response = mapper.toResponse(sample);
        
        return ResponseEntity.ok(response);
    }
}
