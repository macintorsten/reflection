package com.example.reflection.web.dto.v1.request;

import com.example.reflection.domain.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * Request DTO for creating a new Sample.
 * Version 1 API request structure.
 */
@Schema(description = "Request to create a new sample")
public record CreateSampleRequest(
    
    @Schema(description = "Text value", example = "Hello World")
    @NotNull(message = "Text is required")
    @Size(min = 3, max = 100, message = "Text must be between 3 and 100 characters")
    String text,
    
    @Schema(description = "Number value", example = "42")
    @NotNull(message = "Number is required")
    @Min(value = 0, message = "Number must be at least 0")
    @Max(value = 1000, message = "Number must be at most 1000")
    Integer number,
    
    @Schema(description = "Status enum", example = "active")
    @NotNull(message = "Status is required")
    Status status,
    
    @Schema(description = "Extra key-value pairs", example = "{\"foo\":\"1\"}")
    Map<String, String> extras
) {}
