package com.example.reflection;

import java.util.Map;

/**
 * Test fixtures and helper methods for creating test data.
 * Reduces duplication across test classes.
 */
public class SampleTestFixtures {

    /**
     * Creates a basic SampleDTO with required fields.
     */
    public static SampleDTO createBasicDTO(String text, int number, Sample.Status status) {
        SampleDTO dto = new SampleDTO();
        dto.text = text;
        dto.number = number;
        dto.status = status;
        return dto;
    }

    /**
     * Creates a SampleDTO with extras map.
     */
    public static SampleDTO createDTOWithExtras(String text, int number, Sample.Status status, Map<String, String> extras) {
        SampleDTO dto = createBasicDTO(text, number, status);
        dto.extras = extras;
        return dto;
    }

    /**
     * Creates a minimal valid SampleDTO for testing.
     */
    public static SampleDTO createMinimalDTO() {
        return createBasicDTO("Min", 0, Sample.Status.ACTIVE);
    }

    /**
     * Creates a maximal valid SampleDTO for testing.
     */
    public static SampleDTO createMaximalDTO() {
        return createBasicDTO("A".repeat(100), 1000, Sample.Status.INACTIVE);
    }
}
