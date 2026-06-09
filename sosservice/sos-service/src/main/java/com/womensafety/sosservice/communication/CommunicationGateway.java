package com.womensafety.sosservice.communication;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.CommunicationResults;

public interface CommunicationGateway {
    CommunicationResults send(
            ActiveSafetySession session
    );
}
