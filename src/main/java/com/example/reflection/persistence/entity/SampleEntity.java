package com.example.reflection.persistence.entity;

import com.example.reflection.domain.model.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * JPA Entity for Sample persistence.
 * Represents database table structure with ORM mappings.
 * Separated from domain model to keep persistence concerns isolated.
 */
@Entity
@Table(name = "samples")
@Getter
@Setter
@NoArgsConstructor
public class SampleEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String text;
    
    @Column(nullable = false)
    private Integer number;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    /**
     * Transient field - not persisted to database.
     * This field is used to pass additional data through the application layers
     * but is not stored in the database as it's marked @Transient.
     * Consider persisting this field if the data needs to be retained across sessions.
     */
    @Transient
    private Map<String, Integer> mapField;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
