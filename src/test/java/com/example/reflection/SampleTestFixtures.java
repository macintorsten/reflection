package com.example.reflection;

import com.example.reflection.domain.model.Sample;
import com.example.reflection.domain.model.Status;
import com.example.reflection.web.dto.v1.request.CreateSampleRequest;
import com.example.reflection.web.dto.v1.response.SampleResponse;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Test fixtures and helper methods for creating test data.
 * Reduces duplication across test classes.
 * Updated for new layered architecture with domain objects and DTOs.
 */
public class SampleTestFixtures {

    // ========== Request DTO Fixtures ==========

    /**
     * Creates a basic CreateSampleRequest with required fields.
     */
    public static CreateSampleRequest createBasicRequest(String text, int number, Status status) {
        CreateSampleRequest request = new CreateSampleRequest();
        request.setText(text);
        request.setNumber(number);
        request.setStatus(status);
        return request;
    }

    /**
     * Creates a CreateSampleRequest with extras map.
     */
    public static CreateSampleRequest createRequestWithExtras(
            String text, int number, Status status, Map<String, String> extras) {
        CreateSampleRequest request = createBasicRequest(text, number, status);
        request.setExtras(extras);
        return request;
    }

    /**
     * Creates a minimal valid CreateSampleRequest for testing.
     */
    public static CreateSampleRequest createMinimalRequest() {
        return createBasicRequest("Min", 0, Status.ACTIVE);
    }

    /**
     * Creates a maximal valid CreateSampleRequest for testing.
     */
    public static CreateSampleRequest createMaximalRequest() {
        return createBasicRequest("A".repeat(100), 1000, Status.INACTIVE);
    }

    // ========== Domain Object Fixtures ==========

    /**
     * Creates a basic Sample domain object.
     */
    public static Sample createBasicDomain(Long id, String text, int number, Status status) {
        return Sample.builder()
                .id(id)
                .text(text)
                .number(number)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a Sample domain object with mapField.
     */
    public static Sample createDomainWithMapField(
            Long id, String text, int number, Status status, Map<String, Integer> mapField) {
        return Sample.builder()
                .id(id)
                .text(text)
                .number(number)
                .status(status)
                .mapField(mapField)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ========== Response DTO Fixtures ==========

    /**
     * Creates a basic SampleResponse.
     */
    public static SampleResponse createBasicResponse(Long id, String text, int number, Status status) {
        return SampleResponse.builder()
                .id(id)
                .text(text)
                .number(number)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a SampleResponse with extras.
     */
    public static SampleResponse createResponseWithExtras(
            Long id, String text, int number, Status status, Map<String, String> extras) {
        return SampleResponse.builder()
                .id(id)
                .text(text)
                .number(number)
                .status(status)
                .extras(extras)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
