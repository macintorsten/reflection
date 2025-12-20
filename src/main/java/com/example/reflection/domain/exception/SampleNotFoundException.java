package com.example.reflection.domain.exception;

/**
 * Exception thrown when a Sample is not found.
 * Business layer exception for domain operations.
 */
public class SampleNotFoundException extends RuntimeException {
    public SampleNotFoundException(String message) {
        super(message);
    }
}
