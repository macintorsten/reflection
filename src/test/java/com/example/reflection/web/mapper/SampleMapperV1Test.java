package com.example.reflection.web.mapper;

import com.example.reflection.domain.model.Sample;
import com.example.reflection.domain.model.Status;
import com.example.reflection.persistence.entity.SampleEntity;
import com.example.reflection.web.dto.v1.request.CreateSampleRequest;
import com.example.reflection.web.dto.v1.response.SampleResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SampleMapperV1.
 * Tests the three-layer translation: Request DTO → Domain ↔ Entity → Response DTO
 */
class SampleMapperV1Test {

    private final SampleMapperV1 mapper = new SampleMapperV1();

    // ========== Request DTO → Domain Tests ==========

    @Test
    void toDomain_fromRequest_mapsBasicFields() {
        // Arrange
        CreateSampleRequest request = new CreateSampleRequest();
        request.setText("Test");
        request.setNumber(42);
        request.setStatus(Status.ACTIVE);

        // Act
        Sample domain = mapper.toDomain(request);

        // Assert
        assertThat(domain.getText()).isEqualTo("Test");
        assertThat(domain.getNumber()).isEqualTo(42);
        assertThat(domain.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void toDomain_fromRequest_convertsExtrasToMapField() {
        // Arrange - extras has String values, mapField needs Integer
        CreateSampleRequest request = new CreateSampleRequest();
        request.setText("Test");
        request.setNumber(1);
        request.setStatus(Status.ACTIVE);
        request.setExtras(Map.of("priority", "5", "count", "100"));

        // Act
        Sample domain = mapper.toDomain(request);

        // Assert - values are converted to Integers
        assertThat(domain.getMapField()).isNotNull();
        assertThat(domain.getMapField().get("priority")).isEqualTo(5);
        assertThat(domain.getMapField().get("count")).isEqualTo(100);
    }

    @Test
    void toDomain_fromRequest_invalidNumberInExtras_defaultsToZero() {
        // Arrange - "not-a-number" cannot be parsed as Integer
        CreateSampleRequest request = new CreateSampleRequest();
        request.setText("Test");
        request.setNumber(1);
        request.setStatus(Status.INACTIVE);
        request.setExtras(Map.of("invalid", "not-a-number", "valid", "42"));

        // Act
        Sample domain = mapper.toDomain(request);

        // Assert - invalid values default to 0
        assertThat(domain.getMapField().get("invalid")).isEqualTo(0);
        assertThat(domain.getMapField().get("valid")).isEqualTo(42);
    }

    @Test
    void toDomain_fromRequest_nullExtras_mapFieldIsNull() {
        // Arrange
        CreateSampleRequest request = new CreateSampleRequest();
        request.setText("Test");
        request.setNumber(1);
        request.setStatus(Status.ACTIVE);
        request.setExtras(null);

        // Act
        Sample domain = mapper.toDomain(request);

        // Assert
        assertThat(domain.getMapField()).isNull();
    }

    @Test
    void toDomain_fromRequest_emptyExtras_mapFieldIsNull() {
        // Arrange
        CreateSampleRequest request = new CreateSampleRequest();
        request.setText("Test");
        request.setNumber(1);
        request.setStatus(Status.ACTIVE);
        request.setExtras(Map.of());

        // Act
        Sample domain = mapper.toDomain(request);

        // Assert - empty map is treated as absent
        assertThat(domain.getMapField()).isNull();
    }

    // ========== Domain → Entity Tests ==========

    @Test
    void toEntity_fromDomain_mapsAllFields() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test")
            .number(42)
            .status(Status.ACTIVE)
            .mapField(Map.of("key", 123))
            .createdAt(now)
            .updatedAt(now)
            .build();

        // Act
        SampleEntity entity = mapper.toEntity(domain);

        // Assert
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getText()).isEqualTo("Test");
        assertThat(entity.getNumber()).isEqualTo(42);
        assertThat(entity.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(entity.getMapField()).containsEntry("key", 123);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    // ========== Entity → Domain Tests ==========

    @Test
    void toDomain_fromEntity_mapsAllFields() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        SampleEntity entity = new SampleEntity();
        entity.setId(2L);
        entity.setText("Test Entity");
        entity.setNumber(99);
        entity.setStatus(Status.INACTIVE);
        entity.setMapField(Map.of("priority", 5));
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        // Act
        Sample domain = mapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getText()).isEqualTo("Test Entity");
        assertThat(domain.getNumber()).isEqualTo(99);
        assertThat(domain.getStatus()).isEqualTo(Status.INACTIVE);
        assertThat(domain.getMapField()).containsEntry("priority", 5);
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getUpdatedAt()).isEqualTo(now);
    }

    // ========== Domain → Response DTO Tests ==========

    @Test
    void toResponse_fromDomain_mapsBasicFields() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test")
            .number(42)
            .status(Status.INACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .build();

        // Act
        SampleResponse response = mapper.toResponse(domain);

        // Assert
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getText()).isEqualTo("Test");
        assertThat(response.getNumber()).isEqualTo(42);
        assertThat(response.getStatus()).isEqualTo(Status.INACTIVE);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toResponse_fromDomain_convertsMapFieldToExtras() {
        // Arrange - mapField has Integer values, extras needs String
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test")
            .number(1)
            .status(Status.ACTIVE)
            .mapField(Map.of("priority", 5, "count", 100))
            .build();

        // Act
        SampleResponse response = mapper.toResponse(domain);

        // Assert - values are converted to Strings
        assertThat(response.getExtras()).isNotNull();
        assertThat(response.getExtras().get("priority")).isEqualTo("5");
        assertThat(response.getExtras().get("count")).isEqualTo("100");
    }

    @Test
    void toResponse_fromDomain_nullMapField_extrasIsNull() {
        // Arrange
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test")
            .number(1)
            .status(Status.ACTIVE)
            .mapField(null)
            .build();

        // Act
        SampleResponse response = mapper.toResponse(domain);

        // Assert
        assertThat(response.getExtras()).isNull();
    }

    @Test
    void toResponse_fromDomain_emptyMapField_extrasIsNull() {
        // Arrange
        Sample domain = Sample.builder()
            .id(1L)
            .text("Test")
            .number(1)
            .status(Status.ACTIVE)
            .mapField(Map.of())
            .build();

        // Act
        SampleResponse response = mapper.toResponse(domain);

        // Assert
        assertThat(response.getExtras()).isNull();
    }

    // ========== Round-trip Tests ==========

    @Test
    void fullRoundTrip_preservesData() {
        // Arrange - Request DTO
        CreateSampleRequest request = new CreateSampleRequest();
        request.setText("Round Trip Test");
        request.setNumber(999);
        request.setStatus(Status.ACTIVE);
        request.setExtras(Map.of("key1", "10", "key2", "20"));

        // Act - Request → Domain → Entity → Domain → Response
        Sample domainFromRequest = mapper.toDomain(request);
        SampleEntity entity = mapper.toEntity(domainFromRequest);
        Sample domainFromEntity = mapper.toDomain(entity);
        SampleResponse response = mapper.toResponse(domainFromEntity);

        // Assert - data is preserved
        assertThat(response.getText()).isEqualTo(request.getText());
        assertThat(response.getNumber()).isEqualTo(request.getNumber());
        assertThat(response.getStatus()).isEqualTo(request.getStatus());
        assertThat(response.getExtras()).isEqualTo(request.getExtras());
    }

    @Test
    void toResponse_handlesZeroValue() {
        // Arrange - zero is a valid value, not an error
        Sample domain = Sample.builder()
            .id(1L)
            .text("Zero Test")
            .number(0)
            .status(Status.ACTIVE)
            .mapField(Map.of("zero", 0))
            .build();

        // Act
        SampleResponse response = mapper.toResponse(domain);

        // Assert
        assertThat(response.getNumber()).isEqualTo(0);
        assertThat(response.getExtras().get("zero")).isEqualTo("0");
    }

    @Test
    void toDomain_fromRequest_handlesNegativeNumbers() {
        // Arrange
        CreateSampleRequest request = new CreateSampleRequest();
        request.setText("Negative Test");
        request.setNumber(-5);
        request.setStatus(Status.INACTIVE);
        request.setExtras(Map.of("negative", "-10"));

        // Act
        Sample domain = mapper.toDomain(request);

        // Assert
        assertThat(domain.getNumber()).isEqualTo(-5);
        assertThat(domain.getMapField().get("negative")).isEqualTo(-10);
    }
}
