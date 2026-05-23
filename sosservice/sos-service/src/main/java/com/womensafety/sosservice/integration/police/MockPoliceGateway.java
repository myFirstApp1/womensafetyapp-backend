package com.womensafety.sosservice.integration.police;

import com.womensafety.sosservice.dto.PoliceIncidentPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockPoliceGateway
        implements PoliceGateway {

    @Override
    public void send(
            PoliceIncidentPacket packet
    ) {

        log.error(
                "MOCK_POLICE_DISPATCH | trackingId={}",
                packet.getTrackingId()
        );
    }
}