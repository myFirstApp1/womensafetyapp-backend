package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.SosOutbox;
import com.womensafety.sosservice.kafka.NotificationProducer;
import com.womensafety.sosservice.repository.SosOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SosRetryService {

    private final SosOutboxRepository sosOutboxRepository;
    private final NotificationProducer notificationProducer;

    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void sendWithRetry(SosOutbox event) throws ExecutionException, InterruptedException {

        notificationProducer.sendAutomaticSOS(
                event.getUserId().toString(),
                event.getLocation()
        );

        event.setRetryCount(event.getRetryCount() + 1);
        event.setStatus("SENT");

        sosOutboxRepository.save(event);
    }

    @Recover
    public void recover(Exception e, SosOutbox event) {
        log.error("SOS FAILED after retries for userId={}", event.getUserId());

        notificationProducer.sendToDLT(
                event.getUserId().toString(),
                event.getLocation()
        );

        event.setStatus("FAILED");
        sosOutboxRepository.save(event);
    }
}