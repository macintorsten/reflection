package com.example.reflection.web.dto.v2.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Structured metadata for aggregated information in V2 API.
 * Breaking change: Replaces the simple Map<String,String> extras in V1.
 */
@Schema(description = "Aggregate metadata with structured fields")
public record AggregateMetadata(
    
    @Schema(description = "Sum of all related values", example = "150")
    Integer totalValue,
    
    @Schema(description = "Count of related items", example = "5")
    Integer itemCount,
    
    @Schema(description = "Additional tags", example = "[\"tag1\", \"tag2\"]")
    java.util.List<String> tags
) {}
