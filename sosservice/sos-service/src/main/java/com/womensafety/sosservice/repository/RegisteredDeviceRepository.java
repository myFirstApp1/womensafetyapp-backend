package com.womensafety.sosservice.repository;

import com.womensafety.sosservice.domain.RegisteredDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegisteredDeviceRepository
        extends JpaRepository<RegisteredDevice, String> {

    List<RegisteredDevice> findByLastHeartbeatAtBefore(
            LocalDateTime threshold
    );
}