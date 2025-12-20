package com.example.reflection.web.dto.v2.response;

import com.example.reflection.domain.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response DTO for Sample operations - Version 2.
 * Breaking changes from V1:
 * - Replaced 'extras' Map<String,String> with structured 'metadata' AggregateMetadata
 * - Added 'priority' field (Integer)
 * - Changed field structure for better aggregation support
 */
@Schema(description = "Sample response V2 with structured metadata")
public record SampleResponse(
    
    @Schema(description = "Sample ID", example = "1")
    Long id,
    
    @Schema(description = "Text value", example = "Hello World")
    String text,
    
    @Schema(description = "Number value", example = "42")
    Integer number,
    
    @Schema(description = "Status", example = "ACTIVE")
    Status status,
    
    @Schema(description = "Priority level (1-10)", example = "5")
    Integer priority,
    
    @Schema(description = "Structured aggregate metadata - replaces extras from V1")
    AggregateMetadata metadata,
    
    @Schema(description = "Creation timestamp")
    LocalDateTime createdAt,
    
    @Schema(description = "Last update timestamp")
    LocalDateTime updatedAt
) {}
