package com.example.reflection.web.dto.v1.response;

import com.example.reflection.domain.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for Sample operations.
 * Version 1 API response structure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Sample response")
public class SampleResponse {
    
    @Schema(description = "Sample ID", example = "1")
    private Long id;
    
    @Schema(description = "Text value", example = "Hello World")
    private String text;
    
    @Schema(description = "Number value", example = "42")
    private Integer number;
    
    @Schema(description = "Status", example = "active")
    private Status status;
    
    @Schema(description = "Extra key-value pairs", example = "{\"foo\":\"1\"}")
    private Map<String, String> extras;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
