package com.example.reflection.web.mapper;

import com.example.reflection.domain.model.Sample;
import com.example.reflection.persistence.entity.SampleEntity;
import com.example.reflection.web.dto.v2.request.CreateSampleRequest;
import com.example.reflection.web.dto.v2.response.AggregateMetadata;
import com.example.reflection.web.dto.v2.response.SampleResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

/**
 * Mapper for converting between V2 DTOs, domain objects, and entities.
 * Handles the new structured metadata format and priority field.
 */
@Component
public class SampleMapperV2 {
    
    /**
     * Convert V2 request DTO to domain model.
     * Priority defaults to 5 if not provided.
     */
    public Sample toDomain(CreateSampleRequest request) {
        // Store priority in mapField for domain layer
        Integer priorityValue = request.priority() != null ? request.priority() : 5;
        Map<String, Integer> mapField = Map.of("priority", priorityValue);
            
        return Sample.builder()
            .text(request.text())
            .number(request.number())
            .status(request.status())
            .mapField(mapField)
            .build();
    }
    
    /**
     * Convert domain model to V2 response DTO.
     * Creates structured metadata from domain's mapField.
     */
    public SampleResponse toResponse(Sample domain) {
        // Extract priority from mapField
        Integer priority = domain.getMapField() != null 
            ? domain.getMapField().getOrDefault("priority", 5)
            : 5;
        
        // Create structured metadata from mapField
        AggregateMetadata metadata = createMetadata(domain.getMapField());
        
        return new SampleResponse(
            domain.getId(),
            domain.getText(),
            domain.getNumber(),
            domain.getStatus(),
            priority,
            metadata,
            domain.getCreatedAt(),
            domain.getUpdatedAt()
        );
    }
    
    /**
     * Convert domain model to entity for persistence.
     */
    public SampleEntity toEntity(Sample domain) {
        SampleEntity entity = new SampleEntity();
        entity.setId(domain.getId());
        entity.setText(domain.getText());
        entity.setNumber(domain.getNumber());
        entity.setStatus(domain.getStatus());
        
        // Extract and persist priority from mapField
        Integer priority = domain.getMapField() != null 
            ? domain.getMapField().getOrDefault("priority", 5)
            : 5;
        entity.setPriority(priority);
        
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
    
    /**
     * Convert entity to domain model.
     */
    public Sample toDomain(SampleEntity entity) {
        // Get priority from entity, default to 5 if null
        Integer priority = entity.getPriority() != null ? entity.getPriority() : 5;
        Map<String, Integer> mapField = Map.of("priority", priority);
        
        return Sample.builder()
            .id(entity.getId())
            .text(entity.getText())
            .number(entity.getNumber())
            .status(entity.getStatus())
            .mapField(mapField)
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
    
    /**
     * Create structured metadata from mapField.
     * Aggregates values and extracts tags.
     */
    private AggregateMetadata createMetadata(Map<String, Integer> mapField) {
        if (mapField == null || mapField.isEmpty()) {
            return new AggregateMetadata(0, 0, new ArrayList<>());
        }
        
        // Calculate total value (sum of all values except priority)
        int totalValue = mapField.entrySet().stream()
            .filter(e -> !"priority".equals(e.getKey()))
            .mapToInt(Map.Entry::getValue)
            .sum();
        
        // Count items (excluding priority)
        int itemCount = (int) mapField.keySet().stream()
            .filter(k -> !"priority".equals(k))
            .count();
        
        // Extract tags from keys (excluding priority)
        var tags = mapField.keySet().stream()
            .filter(k -> !"priority".equals(k))
            .toList();
        
        return new AggregateMetadata(totalValue, itemCount, tags);
    }
}
