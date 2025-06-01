package com.duikt.fittrack.entity;

import com.duikt.fittrack.domain.enums.Type;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "goals")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GoalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Goal name is required")
    @Size(max = 150, message = "Goal name cannot exceed 150 characters")
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Goal type is required")
    private String type;

    @Positive(message = "Target value must be positive")
    @Column(nullable = false)
    private int targetValue;

    @Future(message = "Deadline must be in the future")
    @Column(nullable = false)
    private LocalDate deadline;

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
