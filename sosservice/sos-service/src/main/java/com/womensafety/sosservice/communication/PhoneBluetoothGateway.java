package com.womensafety.sosservice.communication;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.enums.CommunicationMode;
import com.womensafety.sosservice.domain.CommunicationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PhoneBluetoothGateway
        implements CommunicationGateway {


    @Override
    public CommunicationResults send(
            ActiveSafetySession session
    ) {

        log.info(
                "PHONE_BLUETOOTH_SEND | userId={}",
                session.getUserId()
        );

        return CommunicationResults.builder()
                .success(true)
                .acknowledged(true)
                .mode(
                        CommunicationMode.PHONE_BLUETOOTH
                )
                .reason("PHONE_CONNECTED")
                .build();
    }
}