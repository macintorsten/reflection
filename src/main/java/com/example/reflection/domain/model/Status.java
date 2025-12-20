package com.example.reflection.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status enumeration for Sample domain objects.
 * Extracted from entity to separate domain concerns.
 */
public enum Status {
    ACTIVE("active"),
    INACTIVE("inactive");
    
    private final String value;
    
    Status(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    @JsonCreator
    public static Status fromValue(String value) {
        for (Status s : Status.values()) {
            if (s.value.equals(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status value: " + value);
    }
}
