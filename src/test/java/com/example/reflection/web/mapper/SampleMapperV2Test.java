package com.example.reflection.web.mapper;

import com.example.reflection.domain.model.Sample;
import com.example.reflection.domain.model.Status;
import com.example.reflection.persistence.entity.SampleEntity;
import com.example.reflection.web.dto.v2.request.CreateSampleRequest;
import com.example.reflection.web.dto.v2.response.AggregateMetadata;
import com.example.reflection.web.dto.v2.response.SampleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SampleMapperV2.
 * Tests V2 API mapping with priority field and structured metadata.
 */
class SampleMapperV2Test {

    private final SampleMapperV2 mapper = new SampleMapperV2();

    @Test
    @DisplayName("Request DTO to Domain - with priority")
    void testToDomain_WithPriority() {
        CreateSampleRequest request = new CreateSampleRequest(
            "Test Sample",
            100,
            Status.ACTIVE,
            8
        );

        Sample domain = mapper.toDomain(request);

        assertEquals("Test Sample", domain.getText());
        assertEquals(100, domain.getNumber());
        assertEquals(Status.ACTIVE, domain.getStatus());
        assertNotNull(domain.getMapField());
        assertEquals(8, domain.getMapField().get("priority"));
    }

    @Test
    @DisplayName("Request DTO to Domain - default priority")
    void testToDomain_DefaultPriority() {
        CreateSampleRequest request = new CreateSampleRequest(
            "Test Sample",
            100,
            Status.ACTIVE,
            null // No priority specified
        );

        Sample domain = mapper.toDomain(request);

        assertNotNull(domain.getMapField());
        assertEquals(5, domain.getMapField().get("priority"), "Should default to 5");
    }

    @Test
    @DisplayName("Domain to Response DTO - with priority")
    void testToResponse_WithPriority() {
        LocalDateTime now = LocalDateTime.now();
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test Sample")
            .number(100)
            .status(Status.ACTIVE)
            .mapField(Map.of("priority", 7))
            .createdAt(now)
            .updatedAt(now)
            .build();

        SampleResponse response = mapper.toResponse(domain);

        assertEquals(1L, response.id());
        assertEquals("Test Sample", response.text());
        assertEquals(100, response.number());
        assertEquals(Status.ACTIVE, response.status());
        assertEquals(7, response.priority());
        assertNotNull(response.metadata());
        assertEquals(now, response.createdAt());
        assertEquals(now, response.updatedAt());
    }

    @Test
    @DisplayName("Domain to Response DTO - default priority when null")
    void testToResponse_DefaultPriorityWhenNull() {
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test")
            .number(50)
            .status(Status.INACTIVE)
            .mapField(null) // No mapField
            .build();

        SampleResponse response = mapper.toResponse(domain);

        assertEquals(5, response.priority(), "Should default to 5 when mapField is null");
    }

    @Test
    @DisplayName("Domain to Entity - persists priority")
    void testToEntity_WithPriority() {
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test")
            .number(100)
            .status(Status.ACTIVE)
            .mapField(Map.of("priority", 9))
            .build();

        SampleEntity entity = mapper.toEntity(domain);

        assertEquals(1L, entity.getId());
        assertEquals("Test", entity.getText());
        assertEquals(100, entity.getNumber());
        assertEquals(Status.ACTIVE, entity.getStatus());
        assertEquals(9, entity.getPriority(), "Priority should be extracted and persisted");
    }

    @Test
    @DisplayName("Entity to Domain - loads priority")
    void testToDomain_FromEntity() {
        LocalDateTime now = LocalDateTime.now();
        SampleEntity entity = new SampleEntity();
        entity.setId(1L);
        entity.setText("Test");
        entity.setNumber(100);
        entity.setStatus(Status.ACTIVE);
        entity.setPriority(6);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        Sample domain = mapper.toDomain(entity);

        assertEquals(1L, domain.getId());
        assertEquals("Test", domain.getText());
        assertEquals(100, domain.getNumber());
        assertEquals(Status.ACTIVE, domain.getStatus());
        assertNotNull(domain.getMapField());
        assertEquals(6, domain.getMapField().get("priority"), "Priority should be loaded into mapField");
        assertEquals(now, domain.getCreatedAt());
        assertEquals(now, domain.getUpdatedAt());
    }

    @Test
    @DisplayName("Metadata creation - with additional data")
    void testMetadataCreation() {
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test")
            .number(100)
            .status(Status.ACTIVE)
            .mapField(Map.of(
                "priority", 7,
                "tag1", 10,
                "tag2", 20
            ))
            .build();

        SampleResponse response = mapper.toResponse(domain);

        AggregateMetadata metadata = response.metadata();
        assertNotNull(metadata);
        assertEquals(30, metadata.totalValue(), "Should sum tag1(10) + tag2(20), excluding priority");
        assertEquals(2, metadata.itemCount(), "Should count only tags, not priority");
        assertTrue(metadata.tags().contains("tag1"));
        assertTrue(metadata.tags().contains("tag2"));
        assertFalse(metadata.tags().contains("priority"), "Priority should not be in tags");
    }

    @Test
    @DisplayName("Metadata creation - empty when only priority")
    void testMetadataCreation_OnlyPriority() {
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test")
            .number(100)
            .status(Status.ACTIVE)
            .mapField(Map.of("priority", 5))
            .build();

        SampleResponse response = mapper.toResponse(domain);

        AggregateMetadata metadata = response.metadata();
        assertNotNull(metadata);
        assertEquals(0, metadata.totalValue());
        assertEquals(0, metadata.itemCount());
        assertTrue(metadata.tags().isEmpty());
    }
    
    @Test
    @DisplayName("Metadata creation - handles null mapField")
    void testMetadataCreation_NullMapField() {
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test")
            .number(100)
            .status(Status.ACTIVE)
            .mapField(null)
            .build();

        SampleResponse response = mapper.toResponse(domain);

        AggregateMetadata metadata = response.metadata();
        assertNotNull(metadata, "Metadata should never be null");
        assertEquals(0, metadata.totalValue(), "Should return 0 for null mapField");
        assertEquals(0, metadata.itemCount(), "Should return 0 for null mapField");
        assertTrue(metadata.tags().isEmpty(), "Should return empty list for null mapField");
    }
    
    @Test
    @DisplayName("Metadata creation - handles empty mapField")
    void testMetadataCreation_EmptyMapField() {
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test")
            .number(100)
            .status(Status.ACTIVE)
            .mapField(Map.of())
            .build();

        SampleResponse response = mapper.toResponse(domain);

        AggregateMetadata metadata = response.metadata();
        assertNotNull(metadata, "Metadata should never be null");
        assertEquals(0, metadata.totalValue(), "Should return 0 for empty mapField");
        assertEquals(0, metadata.itemCount(), "Should return 0 for empty mapField");
        assertTrue(metadata.tags().isEmpty(), "Should return empty list for empty mapField");
    }
    
    @Test
    @DisplayName("Entity to Domain - handles null priority with default")
    void testToDomain_FromEntityWithNullPriority() {
        SampleEntity entity = new SampleEntity();
        entity.setId(1L);
        entity.setText("Test");
        entity.setNumber(100);
        entity.setStatus(Status.ACTIVE);
        entity.setPriority(null); // Null priority
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        Sample domain = mapper.toDomain(entity);

        assertNotNull(domain.getMapField());
        assertEquals(5, domain.getMapField().get("priority"), "Should default to 5 when entity priority is null");
    }
    
    @Test
    @DisplayName("Domain to Entity - handles null mapField with default priority")
    void testToEntity_NullMapField() {
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test")
            .number(100)
            .status(Status.ACTIVE)
            .mapField(null)
            .build();

        SampleEntity entity = mapper.toEntity(domain);

        assertEquals(5, entity.getPriority(), "Should default to 5 when mapField is null");
    }
}
