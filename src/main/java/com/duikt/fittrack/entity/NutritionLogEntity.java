package com.duikt.fittrack.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "nutrition_log")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NutritionLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @PastOrPresent(message = "Date must be today or earlier")
    @Column(nullable = false)
    private LocalDate date;

    @PositiveOrZero(message = "Total calories cannot be negative")
    @Column(nullable = false)
    private int totalCalories;

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
