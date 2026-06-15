package com.womensafety.sosservice.service.tracking;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.dto.TrackingIdResponse;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackingSessionLookupService {

    private final ActiveSafetySessionRepository sessionRepository;

    public TrackingIdResponse getTrackingId(UUID userId) {

        ActiveSafetySession session =
                sessionRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Session not found"
                                ));

        return new TrackingIdResponse(
                session.getTrackingId()
        );
    }
}