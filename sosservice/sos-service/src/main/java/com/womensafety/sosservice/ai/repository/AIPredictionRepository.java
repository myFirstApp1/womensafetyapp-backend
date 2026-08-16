package com.womensafety.sosservice.ai.repository;

import com.womensafety.sosservice.ai.entity.AIPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AIPredictionRepository
        extends JpaRepository<AIPrediction, UUID> {
}