package com.example.reflection.web.mapper;

import com.example.reflection.domain.model.Sample;
import com.example.reflection.persistence.entity.SampleEntity;
import com.example.reflection.web.dto.v1.request.CreateSampleRequest;
import com.example.reflection.web.dto.v1.response.SampleResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper for Version 1 API.
 * Handles three-layer translation:
 * - Request DTO → Domain
 * - Domain ↔ Entity
 * - Domain → Response DTO
 */
@Component
public class SampleMapperV1 {
    
    /**
     * Convert Request DTO to Domain object.
     * Converts extras Map<String, String> to mapField Map<String, Integer>.
     */
    public Sample toDomain(CreateSampleRequest request) {
        Sample domain = Sample.builder()
            .text(request.getText())
            .number(request.getNumber())
            .status(request.getStatus())
            .build();
            
        // Convert extras Map<String, String> to Map<String, Integer>
        if (request.getExtras() != null && !request.getExtras().isEmpty()) {
            Map<String, Integer> mapField = request.getExtras().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> {
                        try {
                            return Integer.parseInt(e.getValue());
                        } catch (NumberFormatException ex) {
                            return 0;
                        }
                    }
                ));
            domain.setMapField(mapField);
        }
        
        return domain;
    }
    
    /**
     * Convert Domain object to Entity for persistence.
     */
    public SampleEntity toEntity(Sample domain) {
        SampleEntity entity = new SampleEntity();
        entity.setId(domain.getId());
        entity.setText(domain.getText());
        entity.setNumber(domain.getNumber());
        entity.setStatus(domain.getStatus());
        entity.setMapField(domain.getMapField());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
    
    /**
     * Convert Entity to Domain object.
     */
    public Sample toDomain(SampleEntity entity) {
        return Sample.builder()
            .id(entity.getId())
            .text(entity.getText())
            .number(entity.getNumber())
            .status(entity.getStatus())
            .mapField(entity.getMapField())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
    
    /**
     * Convert Domain object to Response DTO.
     * Converts mapField Map<String, Integer> to extras Map<String, String>.
     */
    public SampleResponse toResponse(Sample domain) {
        SampleResponse response = SampleResponse.builder()
            .id(domain.getId())
            .text(domain.getText())
            .number(domain.getNumber())
            .status(domain.getStatus())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
            
        // Convert mapField Map<String, Integer> to extras Map<String, String>
        if (domain.getMapField() != null && !domain.getMapField().isEmpty()) {
            Map<String, String> extras = domain.getMapField().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> String.valueOf(e.getValue())
                ));
            response.setExtras(extras);
        }
        
        return response;
    }
}
