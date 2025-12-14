package com.example.reflection;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Sample JPA entity and SampleDTO.
 * Handles conversion of transient fields and type mismatches.
 */
@Component
public class SampleMapper {

    /**
     * Convert a SampleDTO to a Sample JPA entity.
     * 
     * Transient fields (mapField, doubleArray, nested, myLinkedList) are NOT set here
     * as they should be managed separately or through the DTO extras field.
     */
    public Sample toEntity(SampleDTO dto) {
        Sample entity = new Sample();
        entity.text = dto.text;
        entity.number = dto.number;
        entity.status = dto.status;
        
        // Convert extras (Map<String, String>) to mapField (Map<String, Integer>)
        // Only set if extras is provided and non-empty
        if (dto.extras != null && !dto.extras.isEmpty()) {
            entity.mapField = dto.extras.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> {
                        try {
                            return Integer.parseInt(e.getValue());
                        } catch (NumberFormatException ex) {
                            // If conversion fails, use 0 as default
                            return 0;
                        }
                    }
                ));
        }
        
        return entity;
    }

    /**
     * Convert a Sample JPA entity to a SampleDTO.
     */
    public SampleDTO toDTO(Sample entity) {
        SampleDTO dto = new SampleDTO();
        dto.text = entity.text;
        dto.number = entity.number;
        dto.status = entity.status;
        
        // Convert mapField (Map<String, Integer>) to extras (Map<String, String>)
        if (entity.mapField != null && !entity.mapField.isEmpty()) {
            dto.extras = entity.mapField.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> String.valueOf(e.getValue())
                ));
        }
        
        return dto;
    }
}
