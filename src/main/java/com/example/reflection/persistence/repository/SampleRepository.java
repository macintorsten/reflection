package com.example.reflection.persistence.repository;

import com.example.reflection.persistence.entity.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Sample entity persistence operations.
 * Provides CRUD operations through Spring Data JPA.
 */
@Repository
public interface SampleRepository extends JpaRepository<SampleEntity, Long> {
}
