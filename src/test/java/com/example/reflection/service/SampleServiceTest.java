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

    @BeforeEach
    void setUp() {
        service = new SampleService(repository, mapper);
    }

    @Test
    void createSample_callsMapperAndRepository() {
        // Arrange
        Sample inputDomain = Sample.builder()
            .text("Test Sample")
            .number(42)
            .status(Status.ACTIVE)
            .build();

        SampleEntity mappedEntity = new SampleEntity();
        mappedEntity.setText("Test Sample");
        mappedEntity.setNumber(42);
        mappedEntity.setStatus(Status.ACTIVE);

        SampleEntity savedEntity = new SampleEntity();
        savedEntity.setId(1L);
        savedEntity.setText("Test Sample");
        savedEntity.setNumber(42);
        savedEntity.setStatus(Status.ACTIVE);

        Sample outputDomain = Sample.builder()
            .id(1L)
            .text("Test Sample")
            .number(42)
            .status(Status.ACTIVE)
            .build();

        when(mapper.toEntity(inputDomain)).thenReturn(mappedEntity);
        when(repository.save(mappedEntity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(outputDomain);

        // Act
        Sample result = service.createSample(inputDomain);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Test Sample");
        assertThat(result.getNumber()).isEqualTo(42);
        assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);

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
        Long id = 1L;
        SampleEntity entity = new SampleEntity();
        entity.setId(id);
        entity.setText("Found Sample");
        entity.setNumber(42);
        entity.setStatus(Status.ACTIVE);

        Sample domain = Sample.builder()
            .id(id)
            .text("Found Sample")
            .number(42)
            .status(Status.ACTIVE)
            .build();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // Act
        Sample result = service.getSampleById(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getText()).isEqualTo("Found Sample");

        verify(repository).findById(id);
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
