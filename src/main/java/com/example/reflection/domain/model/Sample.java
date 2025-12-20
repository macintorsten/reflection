package com.example.reflection.domain.model;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain model for Sample.
 * Pure business object without persistence or presentation concerns.
 * Contains business logic and validation.
 */
public class Sample {
    private Long id;
    private String text;
    private Integer number;
    private Status status;
    private Map<String, Integer> mapField;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Sample() {
    }
    
    public Sample(Long id, String text, Integer number, Status status, 
                  Map<String, Integer> mapField, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.text = text;
        this.number = number;
        this.status = status;
        this.mapField = mapField;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Integer getNumber() {
        return number;
    }
    
    public void setNumber(Integer number) {
        this.number = number;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Map<String, Integer> getMapField() {
        return mapField;
    }
    
    public void setMapField(Map<String, Integer> mapField) {
        this.mapField = mapField;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
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
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Long id;
        private String text;
        private Integer number;
        private Status status;
        private Map<String, Integer> mapField;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder text(String text) {
            this.text = text;
            return this;
        }
        
        public Builder number(Integer number) {
            this.number = number;
            return this;
        }
        
        public Builder status(Status status) {
            this.status = status;
            return this;
        }
        
        public Builder mapField(Map<String, Integer> mapField) {
            this.mapField = mapField;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public Sample build() {
            return new Sample(id, text, number, status, mapField, createdAt, updatedAt);
        }
    }
}
