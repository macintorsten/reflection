package com.example.reflection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Sample operations.
 * Handles business logic for creating and listing samples.
 */
@Service
@Transactional
public class SampleService {

    private final SampleRepository repository;
    private final SampleMapper mapper;

    public SampleService(SampleRepository repository, SampleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Create a new sample from a DTO.
     * 
     * @param dto the SampleDTO containing the data to save
     * @return the saved sample as a DTO
     */
    public SampleDTO createSample(SampleDTO dto) {
        Sample entity = mapper.toEntity(dto);
        Sample saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    /**
     * Retrieve all samples.
     * 
     * @return list of all samples as DTOs
     */
    public List<SampleDTO> listSamples() {
        return repository.findAll().stream()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
    }
}
