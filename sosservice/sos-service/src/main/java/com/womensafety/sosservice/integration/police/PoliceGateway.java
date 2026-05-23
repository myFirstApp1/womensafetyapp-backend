package com.womensafety.sosservice.integration.police;

import com.womensafety.sosservice.dto.PoliceIncidentPacket;

public interface PoliceGateway {

    void send(
            PoliceIncidentPacket packet
    );
}