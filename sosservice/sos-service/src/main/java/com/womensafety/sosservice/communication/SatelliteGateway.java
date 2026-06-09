package com.womensafety.sosservice.communication;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.CommunicationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SatelliteGateway
        implements CommunicationGateway {

    @Override
    public CommunicationResults send(
            ActiveSafetySession session
    ) {

        log.info(
                "SATELLITE_ATTEMPT | userId={}",
                session.getUserId()
        );

        return null;
    }
}