package com.womensafety.sosservice.repository;

import com.womensafety.sosservice.domain.LocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationHistoryRepository
        extends JpaRepository<LocationHistory, Long> {

    List<LocationHistory>
    findTop20ByDeviceIdOrderByCapturedAtDesc(
            String deviceId
    );

    List<LocationHistory>
    findTop10ByDeviceIdOrderByCapturedAtDesc(
            String deviceId
    );

}