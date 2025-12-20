package com.example.reflection.web.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Standardized error response structure for API exceptions.
 * Provides consistent error format across all endpoints.
 */
@Schema(description = "Error response")
public record ErrorResponse(
    
    @Schema(description = "Error message", example = "Validation failed")
    String message,
    
    @Schema(description = "HTTP status code", example = "400")
    int status,
    
    @Schema(description = "Timestamp of the error", example = "2025-12-20T15:00:00Z")
    String timestamp,
    
    @Schema(description = "Field-level validation errors")
    List<FieldError> fieldErrors
) {
    
    public ErrorResponse(String message, int status, String timestamp) {
        this(message, status, timestamp, null);
    }
    
    /**
     * Represents a field-level validation error.
     */
    @Schema(description = "Field validation error")
    public record FieldError(
        
        @Schema(description = "Field name", example = "text")
        String field,
        
        @Schema(description = "Error message", example = "size must be between 3 and 100")
        String message
    ) {}
}
