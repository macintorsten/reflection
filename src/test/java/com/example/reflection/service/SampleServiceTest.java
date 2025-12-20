package com.example.reflection.service;

import com.example.reflection.domain.exception.SampleNotFoundException;
import com.example.reflection.domain.model.Sample;
import com.example.reflection.domain.model.Status;
import com.example.reflection.persistence.entity.SampleEntity;
import com.example.reflection.persistence.repository.SampleRepository;
import com.example.reflection.web.mapper.SampleMapperV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Service layer unit tests for SampleService.
 * Tests service orchestration logic with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class SampleServiceTest {

    @Mock
    private SampleRepository repository;

    @Mock
    private SampleMapperV1 mapper;

    private SampleService service;

    // Test data constants
    private static final String TEST_TEXT = "Test Sample";
    private static final int TEST_NUMBER = 42;
    private static final Status TEST_STATUS = Status.ACTIVE;
    private static final Long TEST_ID = 1L;

    @BeforeEach
    void setUp() {
        service = new SampleService(repository, mapper);
    }

    // ========== Test Data Builders ==========

    private Sample createTestDomain(Long id) {
        return Sample.builder()
            .id(id)
            .text(TEST_TEXT)
            .number(TEST_NUMBER)
            .status(TEST_STATUS)
            .build();
    }

    private SampleEntity createTestEntity(Long id) {
        SampleEntity entity = new SampleEntity();
        entity.setId(id);
        entity.setText(TEST_TEXT);
        entity.setNumber(TEST_NUMBER);
        entity.setStatus(TEST_STATUS);
        return entity;
    }

    @Test
    void createSample_callsMapperAndRepository() {
        // Arrange
        Sample inputDomain = createTestDomain(null);
        SampleEntity mappedEntity = createTestEntity(null);
        SampleEntity savedEntity = createTestEntity(TEST_ID);
        Sample outputDomain = createTestDomain(TEST_ID);

        when(mapper.toEntity(inputDomain)).thenReturn(mappedEntity);
        when(repository.save(mappedEntity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(outputDomain);

        // Act
        Sample result = service.createSample(inputDomain);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_ID);
        assertThat(result.getText()).isEqualTo(TEST_TEXT);
        assertThat(result.getNumber()).isEqualTo(TEST_NUMBER);
        assertThat(result.getStatus()).isEqualTo(TEST_STATUS);

        verify(mapper).toEntity(inputDomain);
        verify(repository).save(mappedEntity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    void createSample_withMapField_passesToMapper() {
        // Arrange
        Sample inputDomain = Sample.builder()
            .text("Sample with map")
            .number(100)
            .status(Status.INACTIVE)
            .mapField(Map.of("priority", 5, "category", 10))
            .build();

        SampleEntity mappedEntity = new SampleEntity();
        mappedEntity.setText("Sample with map");
        mappedEntity.setNumber(100);
        mappedEntity.setStatus(Status.INACTIVE);
        mappedEntity.setMapField(Map.of("priority", 5, "category", 10));

        SampleEntity savedEntity = new SampleEntity();
        savedEntity.setId(2L);
        savedEntity.setText("Sample with map");
        savedEntity.setNumber(100);
        savedEntity.setStatus(Status.INACTIVE);
        savedEntity.setMapField(Map.of("priority", 5, "category", 10));

        Sample outputDomain = Sample.builder()
            .id(2L)
            .text("Sample with map")
            .number(100)
            .status(Status.INACTIVE)
            .mapField(Map.of("priority", 5, "category", 10))
            .build();

        when(mapper.toEntity(inputDomain)).thenReturn(mappedEntity);
        when(repository.save(mappedEntity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(outputDomain);

        // Act
        Sample result = service.createSample(inputDomain);

        // Assert
        assertThat(result.getMapField()).isNotEmpty();
        assertThat(result.getMapField().get("priority")).isEqualTo(5);
        assertThat(result.getMapField().get("category")).isEqualTo(10);

        verify(mapper).toEntity(inputDomain);
        verify(repository).save(any());
        verify(mapper).toDomain(any(SampleEntity.class));
    }

    @Test
    void createSample_invalidText_throwsException() {
        // Arrange - text too short (validation in domain)
        Sample invalidDomain = Sample.builder()
            .text("AB")  // Too short
            .number(42)
            .status(Status.ACTIVE)
            .build();

        // Act & Assert
        assertThatThrownBy(() -> service.createSample(invalidDomain))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Text must be between 3 and 100 characters");

        // Verify no persistence happened
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
    }

    @Test
    void createSample_invalidNumber_throwsException() {
        // Arrange - number out of range
        Sample invalidDomain = Sample.builder()
            .text("Valid Text")
            .number(1001)  // Too high
            .status(Status.ACTIVE)
            .build();

        // Act & Assert
        assertThatThrownBy(() -> service.createSample(invalidDomain))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Number must be between 0 and 1000");

        // Verify no persistence happened
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
    }

    @Test
    void listSamples_returnsAllMappedEntities() {
        // Arrange
        SampleEntity entity1 = new SampleEntity();
        entity1.setId(1L);
        entity1.setText("Sample A");
        entity1.setNumber(10);
        entity1.setStatus(Status.ACTIVE);

        SampleEntity entity2 = new SampleEntity();
        entity2.setId(2L);
        entity2.setText("Sample B");
        entity2.setNumber(20);
        entity2.setStatus(Status.INACTIVE);

        Sample domain1 = Sample.builder()
            .id(1L)
            .text("Sample A")
            .number(10)
            .status(Status.ACTIVE)
            .build();

        Sample domain2 = Sample.builder()
            .id(2L)
            .text("Sample B")
            .number(20)
            .status(Status.INACTIVE)
            .build();

        when(repository.findAll()).thenReturn(List.of(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(domain1);
        when(mapper.toDomain(entity2)).thenReturn(domain2);

        // Act
        List<Sample> result = service.listSamples();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getText()).isEqualTo("Sample A");
        assertThat(result.get(1).getText()).isEqualTo("Sample B");

        verify(repository).findAll();
        verify(mapper, times(2)).toDomain(any(SampleEntity.class));
    }

    @Test
    void listSamples_emptyList_returnsEmptyList() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());

        // Act
        List<Sample> result = service.listSamples();

        // Assert
        assertThat(result).isEmpty();

        verify(repository).findAll();
        verify(mapper, never()).toDomain(any(SampleEntity.class));
    }

    @Test
    void getSampleById_found_returnsDomain() {
        // Arrange
        SampleEntity entity = createTestEntity(TEST_ID);
        Sample domain = createTestDomain(TEST_ID);

        when(repository.findById(TEST_ID)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // Act
        Sample result = service.getSampleById(TEST_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_ID);
        assertThat(result.getText()).isEqualTo(TEST_TEXT);

        verify(repository).findById(TEST_ID);
        verify(mapper).toDomain(entity);
    }

    @Test
    void getSampleById_notFound_throwsException() {
        // Arrange
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.getSampleById(id))
            .isInstanceOf(SampleNotFoundException.class)
            .hasMessageContaining("Sample not found with ID: 999");

        verify(repository).findById(id);
        verify(mapper, never()).toDomain(any(SampleEntity.class));
    }
}
