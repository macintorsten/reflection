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
        // Convert extras Map<String, String> to Map<String, Integer> if present
        Map<String, Integer> mapField = null;
        if (request.extras() != null && !request.extras().isEmpty()) {
            mapField = request.extras().entrySet().stream()
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
        }
        
        return Sample.builder()
            .text(request.text())
            .number(request.number())
            .status(request.status())
            .mapField(mapField)
            .build();
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
        // Convert mapField Map<String, Integer> to extras Map<String, String> if present
        Map<String, String> extras = null;
        if (domain.getMapField() != null && !domain.getMapField().isEmpty()) {
            extras = domain.getMapField().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> String.valueOf(e.getValue())
                ));
        }
        
        return new SampleResponse(
            domain.getId(),
            domain.getText(),
            domain.getNumber(),
            domain.getStatus(),
            extras,
            domain.getCreatedAt(),
            domain.getUpdatedAt()
        );
    }
}
