package com.example.reflection.service;

import com.example.reflection.domain.exception.SampleNotFoundException;
import com.example.reflection.domain.model.Sample;
import com.example.reflection.domain.model.Status;
import com.example.reflection.persistence.entity.SampleEntity;
import com.example.reflection.persistence.repository.SampleRepository;
import com.example.reflection.web.dto.v2.response.StatisticsResponse;
import com.example.reflection.web.mapper.SampleMapperV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SampleService {
    
    private static final Logger log = LoggerFactory.getLogger(SampleService.class);
    
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
    
    /**
     * Get aggregate statistics for all samples.
     * NEW method for V2 API - calculates aggregate data.
     * 
     * @return aggregate statistics
     */
    public StatisticsResponse getStatistics() {
        log.debug("Calculating sample statistics");
        
        List<Sample> allSamples = listSamples();
        
        long totalCount = allSamples.size();
        
        // Calculate average number
        double averageNumber = allSamples.isEmpty() ? 0.0 : 
            allSamples.stream()
                .mapToInt(Sample::getNumber)
                .average()
                .orElse(0.0);
        
        // Calculate total sum
        long totalNumberSum = allSamples.stream()
            .mapToInt(Sample::getNumber)
            .sum();
        
        // Count by status
        java.util.Map<String, Long> countByStatus = allSamples.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                s -> s.getStatus().name(),
                java.util.stream.Collectors.counting()
            ));
        
        // Calculate average priority (default to 5 if no mapField)
        double averagePriority = allSamples.isEmpty() ? 5.0 :
            allSamples.stream()
                .mapToInt(s -> s.getMapField() != null ? 
                    s.getMapField().getOrDefault("priority", 5) : 5)
                .average()
                .orElse(5.0);
        
        log.info("Statistics calculated: {} samples, avg number: {}, avg priority: {}", 
            totalCount, averageNumber, averagePriority);
        
        return new StatisticsResponse(
            totalCount,
            averageNumber,
            totalNumberSum,
            countByStatus,
            averagePriority
        );
    }
}
