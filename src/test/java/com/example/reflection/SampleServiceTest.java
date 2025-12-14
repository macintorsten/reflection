package com.example.reflection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Service layer unit tests for SampleService.
 * 
 * Purpose: Test service orchestration logic with mocked dependencies.
 * These tests verify that the service correctly coordinates between
 * the repository and mapper layers.
 * 
 * What it tests:
 * - Service calls mapper and repository in correct order
 * - Extras are passed through the mapping layer
 * - Empty lists are handled correctly
 * 
 * What it does NOT test:
 * - Mapper logic (see SampleMapperTest)
 * - Database persistence (see SampleControllerIntegrationTest)
 * - Boundary values (tested in integration tests)
 */
@ExtendWith(MockitoExtension.class)
class SampleServiceTest {

    @Mock
    private SampleRepository repository;

    @Mock
    private SampleMapper mapper;

    private SampleService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new SampleService(repository, mapper);
    }

    @Test
    void createSample_callsMapperAndRepository() {
        // Arrange
        SampleDTO inputDTO = new SampleDTO();
        inputDTO.text = "Test Sample";
        inputDTO.number = 42;
        inputDTO.status = Sample.Status.ACTIVE;

        Sample mappedEntity = new Sample();
        mappedEntity.text = "Test Sample";
        mappedEntity.number = 42;
        mappedEntity.status = Sample.Status.ACTIVE;

        Sample savedEntity = new Sample();
        savedEntity.id = 1L;
        savedEntity.text = "Test Sample";
        savedEntity.number = 42;
        savedEntity.status = Sample.Status.ACTIVE;

        SampleDTO outputDTO = new SampleDTO();
        outputDTO.text = "Test Sample";
        outputDTO.number = 42;
        outputDTO.status = Sample.Status.ACTIVE;

        when(mapper.toEntity(inputDTO)).thenReturn(mappedEntity);
        when(repository.save(mappedEntity)).thenReturn(savedEntity);
        when(mapper.toDTO(savedEntity)).thenReturn(outputDTO);

        // Act
        SampleDTO result = service.createSample(inputDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.text).isEqualTo("Test Sample");
        assertThat(result.number).isEqualTo(42);
        assertThat(result.status).isEqualTo(Sample.Status.ACTIVE);

        verify(mapper).toEntity(inputDTO);
        verify(repository).save(mappedEntity);
        verify(mapper).toDTO(savedEntity);
    }

    @Test
    void createSample_withExtras_passesToMapper() {
        // Arrange
        SampleDTO inputDTO = new SampleDTO();
        inputDTO.text = "Sample with extras";
        inputDTO.number = 100;
        inputDTO.status = Sample.Status.INACTIVE;
        inputDTO.extras = Map.of("priority", "5", "category", "10");

        Sample mappedEntity = new Sample();
        mappedEntity.text = "Sample with extras";
        mappedEntity.number = 100;
        mappedEntity.status = Sample.Status.INACTIVE;
        mappedEntity.mapField = Map.of("priority", 5, "category", 10);

        Sample savedEntity = new Sample();
        savedEntity.id = 2L;
        savedEntity.text = "Sample with extras";
        savedEntity.number = 100;
        savedEntity.status = Sample.Status.INACTIVE;
        savedEntity.mapField = Map.of("priority", 5, "category", 10);

        SampleDTO outputDTO = new SampleDTO();
        outputDTO.text = "Sample with extras";
        outputDTO.number = 100;
        outputDTO.status = Sample.Status.INACTIVE;
        outputDTO.extras = Map.of("priority", "5", "category", "10");

        when(mapper.toEntity(inputDTO)).thenReturn(mappedEntity);
        when(repository.save(mappedEntity)).thenReturn(savedEntity);
        when(mapper.toDTO(savedEntity)).thenReturn(outputDTO);

        // Act
        SampleDTO result = service.createSample(inputDTO);

        // Assert
        assertThat(result.extras).isNotEmpty();
        assertThat(result.extras.get("priority")).isEqualTo("5");
        assertThat(result.extras.get("category")).isEqualTo("10");

        verify(mapper).toEntity(inputDTO);
        verify(repository).save(any());
        verify(mapper).toDTO(any());
    }

    @Test
    void listSamples_returnsAllMappedEntities() {
        // Arrange
        Sample entity1 = new Sample();
        entity1.id = 1L;
        entity1.text = "Sample A";
        entity1.number = 10;
        entity1.status = Sample.Status.ACTIVE;

        Sample entity2 = new Sample();
        entity2.id = 2L;
        entity2.text = "Sample B";
        entity2.number = 20;
        entity2.status = Sample.Status.INACTIVE;

        SampleDTO dto1 = new SampleDTO();
        dto1.text = "Sample A";
        dto1.number = 10;
        dto1.status = Sample.Status.ACTIVE;

        SampleDTO dto2 = new SampleDTO();
        dto2.text = "Sample B";
        dto2.number = 20;
        dto2.status = Sample.Status.INACTIVE;

        when(repository.findAll()).thenReturn(List.of(entity1, entity2));
        when(mapper.toDTO(entity1)).thenReturn(dto1);
        when(mapper.toDTO(entity2)).thenReturn(dto2);

        // Act
        List<SampleDTO> result = service.listSamples();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).text).isEqualTo("Sample A");
        assertThat(result.get(1).text).isEqualTo("Sample B");

        verify(repository).findAll();
        verify(mapper, times(2)).toDTO(any());
    }

    @Test
    void listSamples_emptyList_returnsEmptyList() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());

        // Act
        List<SampleDTO> result = service.listSamples();

        // Assert
        assertThat(result).isEmpty();

        verify(repository).findAll();
        verify(mapper, never()).toDTO(any());
    }
}
