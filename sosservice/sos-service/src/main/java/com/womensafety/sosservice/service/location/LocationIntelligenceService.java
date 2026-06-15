package com.womensafety.sosservice.service.location;

import com.womensafety.sosservice.domain.LocationAnalysisResult;
import com.womensafety.sosservice.domain.enums.LocationEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class LocationIntelligenceService {

    public LocationAnalysisResult analyze(
            Double latitude,
            Double longitude,
            LocalDateTime lastLocationUpdate
    ) {

        if (latitude == null || longitude == null) {

            return new LocationAnalysisResult(
                    LocationEventType.GPS_LOST,
                    50,
                    false
            );
        }

        if (lastLocationUpdate != null &&
                lastLocationUpdate.isBefore(
                        LocalDateTime.now().minusMinutes(20)
                )) {

            return new LocationAnalysisResult(
                    LocationEventType.GPS_FROZEN,
                    40,
                    false
            );
        }

        return new LocationAnalysisResult(
                LocationEventType.NORMAL,
                0,
                false
        );
    }
}