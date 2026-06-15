package com.womensafety.sosservice.service.communication;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.enums.CommunicationMode;

/**
 * Service for deciding which communication channel to use for emergency notifications.
 * Attempts communication via multiple fallback channels: phone, relay, LoRa, satellite.
 */
public interface ICommunicationDecisionService {

    /**
     * Attempt communication through available channels in fallback order.
     * Tries phone/bluetooth first, then relay, LoRa, and finally satellite.
     *
     * @param session the active safety session
     * @return the communication mode successfully used
     */
    CommunicationMode attemptCommunication(ActiveSafetySession session);
}
