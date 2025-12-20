package com.example.reflection.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain model for Sample.
 * Pure business object without persistence or presentation concerns.
 * Contains business logic and validation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sample {
    private Long id;
    private String text;
    private Integer number;
    private Status status;
    private Map<String, Integer> mapField;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Business logic: Check if sample is active.
     */
    public boolean isActive() {
        return Status.ACTIVE.equals(status);
    }
    
    /**
     * Business validation logic.
     * Validates that all required fields meet business rules.
     * 
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (text == null || text.length() < 3 || text.length() > 100) {
            throw new IllegalArgumentException("Text must be between 3 and 100 characters");
        }
        if (number == null || number < 0 || number > 1000) {
            throw new IllegalArgumentException("Number must be between 0 and 1000");
        }
    }
}
