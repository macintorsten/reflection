package com.example.reflection.persistence.entity;

import com.example.reflection.domain.model.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * JPA Entity for Sample persistence.
 * Represents database table structure with ORM mappings.
 * Separated from domain model to keep persistence concerns isolated.
 */
@Entity
@Table(name = "samples")
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
    
    public SampleEntity() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Integer getNumber() {
        return number;
    }
    
    public void setNumber(Integer number) {
        this.number = number;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Map<String, Integer> getMapField() {
        return mapField;
    }
    
    public void setMapField(Map<String, Integer> mapField) {
        this.mapField = mapField;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
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
