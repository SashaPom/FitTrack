package com.duikt.fittrack.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "progress_tracker")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProgressTrackerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @PastOrPresent(message = "Date must be today or earlier")
    @Column(nullable = false)
    private LocalDate date;

    @Positive(message = "Weight must be positive")
    @Column(nullable = false)
    private double weight;

    @DecimalMin(value = "0.0", message = "Fat percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Fat percentage cannot exceed 100%")
    @Column(nullable = false)
    private double fatPercentage;

    @Positive(message = "Muscle mass must be positive")
    @Column(nullable = false)
    private double muscleMass;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
