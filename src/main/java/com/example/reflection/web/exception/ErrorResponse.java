package com.example.reflection.web.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Standardized error response structure for API exceptions.
 * Provides consistent error format across all endpoints.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response")
public class ErrorResponse {
    
    @Schema(description = "Error message", example = "Validation failed")
    private String message;
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "Timestamp of the error", example = "2025-12-20T15:00:00Z")
    private String timestamp;
    
    @Schema(description = "Field-level validation errors")
    private List<FieldError> fieldErrors;
    
    public ErrorResponse(String message, int status, String timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }
    
    /**
     * Represents a field-level validation error.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Field validation error")
    public static class FieldError {
        
        @Schema(description = "Field name", example = "text")
        private String field;
        
        @Schema(description = "Error message", example = "size must be between 3 and 100")
        private String message;
    }
}
