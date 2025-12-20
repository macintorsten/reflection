package com.example.reflection.web.controller.v2;

import com.example.reflection.domain.model.Sample;
import com.example.reflection.service.SampleService;
import com.example.reflection.web.dto.v2.request.CreateSampleRequest;
import com.example.reflection.web.dto.v2.response.SampleResponse;
import com.example.reflection.web.dto.v2.response.StatisticsResponse;
import com.example.reflection.web.exception.ErrorResponse;
import com.example.reflection.web.mapper.SampleMapperV2;
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
 * REST Controller for Sample operations - Version 2.
 * Breaking changes from V1:
 * - Response format changed with structured metadata instead of extras map
 * - Added priority field to requests and responses
 * - New statistics aggregation endpoint
 */
@RestController
@RequestMapping("/api/v2/samples")
@Tag(name = "Samples V2", description = "Version 2 of Sample operations with aggregation support")
public class SampleControllerV2 {
    
    private static final Logger log = LoggerFactory.getLogger(SampleControllerV2.class);
    
    private final SampleService sampleService;
    private final SampleMapperV2 mapper;
    
    public SampleControllerV2(SampleService sampleService, SampleMapperV2 mapper) {
        this.sampleService = sampleService;
        this.mapper = mapper;
    }
    
    /**
     * Create a new sample with priority support.
     * 
     * @param request the request DTO with sample data including priority
     * @return the created sample as V2 response DTO
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a sample (V2)", description = "Creates a new Sample with priority support")
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
        log.info("V2: Received request to create sample: {}", request);
        
        // Request DTO → Domain
        Sample domain = mapper.toDomain(request);
        
        // Service processes domain object
        Sample created = sampleService.createSample(domain);
        
        // Domain → V2 Response DTO
        SampleResponse response = mapper.toResponse(created);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * List all samples in V2 format.
     * 
     * @return list of all samples as V2 response DTOs
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List samples (V2)", description = "Returns all Samples with V2 response format")
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
        log.info("V2: Received request to list all samples");
        
        List<Sample> samples = sampleService.listSamples();
        List<SampleResponse> responses = samples.stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Get a sample by ID in V2 format.
     * 
     * @param id the sample ID
     * @return the sample as V2 response DTO
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get sample by ID (V2)", description = "Returns a sample in V2 format")
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
        log.info("V2: Received request to get sample with ID: {}", id);
        
        Sample sample = sampleService.getSampleById(id);
        SampleResponse response = mapper.toResponse(sample);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get aggregate statistics for all samples.
     * NEW endpoint in V2 API.
     * 
     * @return aggregate statistics
     */
    @GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get sample statistics", description = "Returns aggregate statistics for all samples (NEW in V2)")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StatisticsResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<StatisticsResponse> getStatistics() {
        log.info("V2: Received request for sample statistics");
        
        StatisticsResponse statistics = sampleService.getStatistics();
        
        return ResponseEntity.ok(statistics);
    }
}
