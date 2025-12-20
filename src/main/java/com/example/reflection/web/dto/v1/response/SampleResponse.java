package com.example.reflection.web.dto.v1.response;

import com.example.reflection.domain.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for Sample operations.
 * Version 1 API response structure.
 */
@Schema(description = "Sample response")
public record SampleResponse(
    
    @Schema(description = "Sample ID", example = "1")
    Long id,
    
    @Schema(description = "Text value", example = "Hello World")
    String text,
    
    @Schema(description = "Number value", example = "42")
    Integer number,
    
    @Schema(description = "Status", example = "active")
    Status status,
    
    @Schema(description = "Extra key-value pairs", example = "{\"foo\":\"1\"}")
    Map<String, String> extras,
    
    @Schema(description = "Creation timestamp")
    LocalDateTime createdAt,
    
    @Schema(description = "Last update timestamp")
    LocalDateTime updatedAt
) {}
