package com.example.reflection;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Standard error response with validation details")
public class ErrorResponse {
    @Schema(description = "Error message", example = "Validation failed")
    public String message;

    @Schema(description = "HTTP status code", example = "400")
    public int status;

    @Schema(description = "Timestamp when error occurred (ISO-8601)")
    public String timestamp;

    @io.swagger.v3.oas.annotations.media.ArraySchema(
            schema = @Schema(implementation = FieldError.class),
            arraySchema = @Schema(description = "List of field-level validation errors (null for non-validation errors)", nullable = true)
    )
    public List<FieldError> fieldErrors;

    public ErrorResponse(String message, int status, String timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.fieldErrors = null;
    }

    public ErrorResponse(String message, int status, String timestamp, List<FieldError> fieldErrors) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.fieldErrors = fieldErrors;
    }

    @Schema(description = "Individual field validation error")
    public static class FieldError {
        @Schema(description = "Field name", example = "text")
        public String field;

        @Schema(description = "Validation error message", example = "size must be between 3 and 100")
        public String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
