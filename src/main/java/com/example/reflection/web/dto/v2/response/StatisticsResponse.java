package com.example.reflection.web.dto.v2.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * Response DTO for aggregate statistics endpoint.
 * New endpoint in V2 API: GET /api/v2/samples/statistics
 */
@Schema(description = "Aggregate statistics for all samples")
public record StatisticsResponse(
    
    @Schema(description = "Total number of samples", example = "42")
    Long totalCount,
    
    @Schema(description = "Average number value across all samples", example = "123.45")
    Double averageNumber,
    
    @Schema(description = "Sum of all number values", example = "5185")
    Long totalNumberSum,
    
    @Schema(description = "Count by status", example = "{\"ACTIVE\": 30, \"INACTIVE\": 12}")
    Map<String, Long> countByStatus,
    
    @Schema(description = "Average priority across all samples", example = "5.5")
    Double averagePriority
) {}
