package com.example.reflection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/samples")
public class SampleController {

    @Autowired
    private SampleRepository repository;

    @PostMapping
    public ResponseEntity<SampleDTO> createSample(@Valid @RequestBody SampleDTO dto) {
        Sample entity = new Sample();
        entity.text = dto.text;
        entity.number = dto.number;
        entity.status = dto.status;
        entity.mapField = dto.extras == null ? null : dto.extras.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Integer.valueOf(e.getValue())));
        entity.doubleArray = null; // not persisted
        Sample saved = repository.save(entity);
        SampleDTO response = new SampleDTO();
        response.text = saved.text;
        response.number = saved.number;
        response.status = saved.status;
        response.extras = dto.extras;
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<SampleDTO> listSamples() {
        return repository.findAll().stream().map(entity -> {
            SampleDTO dto = new SampleDTO();
            dto.text = entity.text;
            dto.number = entity.number;
            dto.status = entity.status;
            dto.extras = entity.mapField == null ? null : entity.mapField.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
            return dto;
        }).collect(Collectors.toList());
    }
}
