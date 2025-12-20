package com.example.reflection.web.dto.v2.request;

import com.example.reflection.domain.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * Request DTO for creating a sample - Version 2.
 * Adds priority field compared to V1.
 */
@Schema(description = "Request to create a new sample V2")
public record CreateSampleRequest(
    
    @NotBlank(message = "Text is required")
    @Size(min = 3, max = 100, message = "Text must be between 3 and 100 characters")
    @Schema(description = "Text value", example = "Hello World", requiredMode = Schema.RequiredMode.REQUIRED)
    String text,
    
    @NotNull(message = "Number is required")
    @Min(value = 0, message = "Number must be at least 0")
    @Max(value = 1000, message = "Number must be at most 1000")
    @Schema(description = "Number value", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer number,
    
    @NotNull(message = "Status is required")
    @Schema(description = "Status (ACTIVE or INACTIVE)", example = "ACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
    Status status,
    
    @Min(value = 1, message = "Priority must be between 1 and 10")
    @Max(value = 10, message = "Priority must be between 1 and 10")
    @Schema(description = "Priority level (1-10)", example = "5")
    Integer priority
) {}
