package com.example.reflection;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SampleMapper.
 * 
 * Tests the DTO ↔ Entity conversion logic in isolation.
 * These tests are fast and focused on the mapping behavior.
 * 
 * What it tests:
 * - Field mapping (text, number, status)
 * - Type conversion (Map<String, String> → Map<String, Integer>)
 * - Null handling
 * - Edge cases (empty maps, invalid number strings)
 * 
 * What it does NOT test:
 * - Database persistence
 * - HTTP layer
 * - Validation (handled by Spring)
 */
class SampleMapperTest {

    private final SampleMapper mapper = new SampleMapper();

    // ========== toEntity Tests ==========

    @Test
    void toEntity_mapsBasicFields() {
        // Arrange
        SampleDTO dto = new SampleDTO();
        dto.text = "Test";
        dto.number = 42;
        dto.status = Sample.Status.ACTIVE;

        // Act
        Sample entity = mapper.toEntity(dto);

        // Assert
        assertThat(entity.text).isEqualTo("Test");
        assertThat(entity.number).isEqualTo(42);
        assertThat(entity.status).isEqualTo(Sample.Status.ACTIVE);
    }

    @Test
    void toEntity_convertsExtrasToMapField() {
        // Arrange - extras has String values, mapField needs Integer
        SampleDTO dto = new SampleDTO();
        dto.text = "Test";
        dto.number = 1;
        dto.status = Sample.Status.ACTIVE;
        dto.extras = Map.of("priority", "5", "count", "100");

        // Act
        Sample entity = mapper.toEntity(dto);

        // Assert - values are converted to Integers
        assertThat(entity.mapField).isNotNull();
        assertThat(entity.mapField.get("priority")).isEqualTo(5);
        assertThat(entity.mapField.get("count")).isEqualTo(100);
    }

    @Test
    void toEntity_invalidNumberInExtras_defaultsToZero() {
        // Arrange - "not-a-number" cannot be parsed as Integer
        SampleDTO dto = new SampleDTO();
        dto.text = "Test";
        dto.number = 1;
        dto.status = Sample.Status.INACTIVE;
        dto.extras = Map.of("invalid", "not-a-number", "valid", "42");

        // Act
        Sample entity = mapper.toEntity(dto);

        // Assert - invalid values default to 0
        assertThat(entity.mapField.get("invalid")).isEqualTo(0);
        assertThat(entity.mapField.get("valid")).isEqualTo(42);
    }

    @Test
    void toEntity_nullExtras_mapFieldIsNull() {
        // Arrange
        SampleDTO dto = new SampleDTO();
        dto.text = "Test";
        dto.number = 1;
        dto.status = Sample.Status.ACTIVE;
        dto.extras = null;

        // Act
        Sample entity = mapper.toEntity(dto);

        // Assert
        assertThat(entity.mapField).isNull();
    }

    @Test
    void toEntity_emptyExtras_mapFieldIsNull() {
        // Arrange
        SampleDTO dto = new SampleDTO();
        dto.text = "Test";
        dto.number = 1;
        dto.status = Sample.Status.ACTIVE;
        dto.extras = Map.of();

        // Act
        Sample entity = mapper.toEntity(dto);

        // Assert - empty map is treated as absent
        assertThat(entity.mapField).isNull();
    }

    // ========== toDTO Tests ==========

    @Test
    void toDTO_mapsBasicFields() {
        // Arrange
        Sample entity = new Sample();
        entity.text = "Test";
        entity.number = 42;
        entity.status = Sample.Status.INACTIVE;

        // Act
        SampleDTO dto = mapper.toDTO(entity);

        // Assert
        assertThat(dto.text).isEqualTo("Test");
        assertThat(dto.number).isEqualTo(42);
        assertThat(dto.status).isEqualTo(Sample.Status.INACTIVE);
    }

    @Test
    void toDTO_convertsMapFieldToExtras() {
        // Arrange - mapField has Integer values, extras needs String
        Sample entity = new Sample();
        entity.text = "Test";
        entity.number = 1;
        entity.status = Sample.Status.ACTIVE;
        entity.mapField = Map.of("priority", 5, "count", 100);

        // Act
        SampleDTO dto = mapper.toDTO(entity);

        // Assert - values are converted to Strings
        assertThat(dto.extras).isNotNull();
        assertThat(dto.extras.get("priority")).isEqualTo("5");
        assertThat(dto.extras.get("count")).isEqualTo("100");
    }

    @Test
    void toDTO_nullMapField_extrasIsNull() {
        // Arrange
        Sample entity = new Sample();
        entity.text = "Test";
        entity.number = 1;
        entity.status = Sample.Status.ACTIVE;
        entity.mapField = null;

        // Act
        SampleDTO dto = mapper.toDTO(entity);

        // Assert
        assertThat(dto.extras).isNull();
    }

    @Test
    void toDTO_emptyMapField_extrasIsNull() {
        // Arrange
        Sample entity = new Sample();
        entity.text = "Test";
        entity.number = 1;
        entity.status = Sample.Status.ACTIVE;
        entity.mapField = Map.of();

        // Act
        SampleDTO dto = mapper.toDTO(entity);

        // Assert
        assertThat(dto.extras).isNull();
    }

    // ========== Round-trip Tests ==========

    @Test
    void roundTrip_preservesData() {
        // Arrange
        SampleDTO originalDTO = new SampleDTO();
        originalDTO.text = "Round Trip Test";
        originalDTO.number = 999;
        originalDTO.status = Sample.Status.ACTIVE;
        originalDTO.extras = Map.of("key1", "10", "key2", "20");

        // Act - DTO → Entity → DTO
        Sample entity = mapper.toEntity(originalDTO);
        SampleDTO resultDTO = mapper.toDTO(entity);

        // Assert - data is preserved
        assertThat(resultDTO.text).isEqualTo(originalDTO.text);
        assertThat(resultDTO.number).isEqualTo(originalDTO.number);
        assertThat(resultDTO.status).isEqualTo(originalDTO.status);
        assertThat(resultDTO.extras).isEqualTo(originalDTO.extras);
    }

    @Test
    void toDTO_handlesZeroValue() {
        // Arrange - zero is a valid value, not an error
        Sample entity = new Sample();
        entity.text = "Zero Test";
        entity.number = 0;
        entity.status = Sample.Status.ACTIVE;
        entity.mapField = Map.of("zero", 0);

        // Act
        SampleDTO dto = mapper.toDTO(entity);

        // Assert
        assertThat(dto.number).isEqualTo(0);
        assertThat(dto.extras.get("zero")).isEqualTo("0");
    }

    @Test
    void toEntity_handlesNegativeNumbers() {
        // Arrange
        SampleDTO dto = new SampleDTO();
        dto.text = "Negative Test";
        dto.number = -5;  // negative number
        dto.status = Sample.Status.INACTIVE;
        dto.extras = Map.of("negative", "-10");

        // Act
        Sample entity = mapper.toEntity(dto);

        // Assert
        assertThat(entity.number).isEqualTo(-5);
        assertThat(entity.mapField.get("negative")).isEqualTo(-10);
    }
}
