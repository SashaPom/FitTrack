package com.duikt.fittrack.repository;

import com.duikt.fittrack.entity.NutritionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NutritionLogRepository extends JpaRepository<NutritionLogEntity, UUID> {
}
