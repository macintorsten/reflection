package com.example.reflection.service;

import com.example.reflection.domain.exception.SampleNotFoundException;
import com.example.reflection.domain.model.Sample;
import com.example.reflection.persistence.entity.SampleEntity;
import com.example.reflection.persistence.repository.SampleRepository;
import com.example.reflection.web.mapper.SampleMapperV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Sample operations.
 * Works exclusively with domain objects.
 * Handles business logic and orchestration.
 */
@Service
@Transactional
@Slf4j
public class SampleService {
    
    private final SampleRepository repository;
    private final SampleMapperV1 mapper;
    
    public SampleService(SampleRepository repository, SampleMapperV1 mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    
    /**
     * Create a new sample.
     * Validates business rules before persistence.
     * 
     * @param sample the domain object to create
     * @return the created domain object with generated ID
     */
    public Sample createSample(Sample sample) {
        log.debug("Creating sample: {}", sample);
        
        // Business validation
        sample.validate();
        
        // Convert to entity and save
        SampleEntity entity = mapper.toEntity(sample);
        SampleEntity savedEntity = repository.save(entity);
        
        // Convert back to domain
        Sample created = mapper.toDomain(savedEntity);
        log.info("Created sample with ID: {}", created.getId());
        return created;
    }
    
    /**
     * List all samples.
     * 
     * @return list of all samples as domain objects
     */
    public List<Sample> listSamples() {
        log.debug("Listing all samples");
        return repository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    /**
     * Get a sample by ID.
     * 
     * @param id the sample ID
     * @return the domain object
     * @throws SampleNotFoundException if sample not found
     */
    public Sample getSampleById(Long id) {
        log.debug("Getting sample by ID: {}", id);
        return repository.findById(id)
            .map(mapper::toDomain)
            .orElseThrow(() -> new SampleNotFoundException("Sample not found with ID: " + id));
    }
}
