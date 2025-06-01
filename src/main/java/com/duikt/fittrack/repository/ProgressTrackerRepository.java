package com.duikt.fittrack.repository;

import com.duikt.fittrack.entity.ProgressTrackerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProgressTrackerRepository extends JpaRepository<ProgressTrackerEntity, UUID> {
    List<ProgressTrackerEntity> findByDateBetween(LocalDate from, LocalDate to);
}
