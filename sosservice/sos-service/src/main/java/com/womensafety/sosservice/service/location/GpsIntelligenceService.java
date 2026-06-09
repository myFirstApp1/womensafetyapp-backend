package com.womensafety.sosservice.service.location;

import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.domain.enums.GpsStatus;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.LocationHistoryRepository;
import com.womensafety.sosservice.service.incident.IncidentResponseService;
import com.womensafety.sosservice.service.core.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GpsIntelligenceService implements IGpsIntelligenceService {

    private final LocationHistoryRepository
            locationHistoryRepository;
    private final IncidentResponseService
            incidentResponseService;

    private final ActiveSafetySessionRepository
            activeSafetySessionRepository;
    private final SessionManager sessionManager;

    public GpsAnalysisResult analyze(
            String deviceId
    ) {

        List<LocationHistory> history =
                locationHistoryRepository
                        .findTop10ByDeviceIdOrderByCapturedAtDesc(
                                deviceId
                        );

        if (history.size() < 2) {

            return new GpsAnalysisResult(
                    GpsStatus.LOCATION_UNKNOWN,
                    0,
                    "Not enough GPS data"
            );
        }

        LocationHistory latest =
                history.get(0);

        LocationHistory oldest =
                history.get(
                        history.size() - 1
                );

        boolean sameLocation =
                latest.getLatitude().compareTo(
                        oldest.getLatitude()
                ) == 0
                        &&
                        latest.getLongitude().compareTo(
                                oldest.getLongitude()
                        ) == 0;

        long minutesStopped =
                Duration.between(
                        oldest.getCapturedAt(),
                        latest.getCapturedAt()
                ).toMinutes();

        if (sameLocation) {

            if (minutesStopped >= 30) {

                ActiveSafetySession session =
                        getSession(deviceId);
                if (session != null) {

                    if (session.getLastGpsStatus()
                            != GpsStatus.STATIONARY_LONG_TIME) {

                        incidentResponseService.processIncident(
                                session,
                                "STATIONARY_LONG_TIME",
                                20,
                                false
                        );

                        session.setLastGpsStatus(
                                GpsStatus.STATIONARY_LONG_TIME
                        );

                        sessionManager.save(session);
                    }
                }
                return new GpsAnalysisResult(
                        GpsStatus.STATIONARY_LONG_TIME,
                        20,
                        "User stationary for "
                                + minutesStopped
                                + " minutes"
                );
            }

            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {

                if (session.getLastGpsStatus()
                        != GpsStatus.STATIONARY) {

                    incidentResponseService.processIncident(
                            session,
                            "STATIONARY",
                            5,
                            false
                    );

                    session.setLastGpsStatus(
                            GpsStatus.STATIONARY
                    );

                    sessionManager.save(session);
                }
            }

            return new GpsAnalysisResult(
                    GpsStatus.STATIONARY,
                    5,
                    "User temporarily stationary"
            );
        }
        ActiveSafetySession session =
                getSession(deviceId);

        if (session != null &&
                session.getLastGpsStatus() != GpsStatus.MOVING) {

            session.setLastGpsStatus(
                    GpsStatus.MOVING
            );

            sessionManager.save(session);
        }
        return new GpsAnalysisResult(
                GpsStatus.MOVING,
                0,
                "User moving normally"
        );
    }

    private ActiveSafetySession getSession(
            String deviceId
    ) {

        return activeSafetySessionRepository
                .findByDeviceId(deviceId)
                .orElse(null);
    }
}
