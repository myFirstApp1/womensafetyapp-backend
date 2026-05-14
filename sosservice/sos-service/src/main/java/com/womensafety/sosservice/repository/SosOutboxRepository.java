package com.womensafety.sosservice.repository;

import com.womensafety.sosservice.domain.SosOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SosOutboxRepository extends JpaRepository<SosOutbox, Long> {

    List<SosOutbox> findByStatus(String status);
    List<SosOutbox> findTop100ByStatusOrderByCreatedAtAsc(String status);


}