package com.womensafety.notificationservice.consumer;

import com.tl.womensafety.common.dto.NotificationRequest;
import com.womensafety.notificationservice.service.TwilioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SosNotificationConsumer {

    private final TwilioService twilioService;

    /**
     * SOS notification consumer.
     *
     * Retry flow:
     *
     * sos-topic
     *    ↓
     * attempt 1
     *    ↓ 3 sec
     * retry
     *    ↓ 6 sec
     * retry
     *    ↓ 12 sec
     * retry
     *    ↓
     * sos-topic-dlt
     */
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(
                    delay = 3000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = "sos-topic",
            groupId = "notification-service-group"
    )
    public void consume(NotificationRequest request) {

        String phone = request.metadata().get("to");
        String message = request.message();

        log.info(
                "SOS MESSAGE RECEIVED | userId={} | channel={} | phone={}",
                request.userId(),
                request.channel(),
                phone
        );

        try {

            switch (request.channel()) {

                case SMS -> {
                    log.info(
                            "Sending SOS SMS | userId={} | phone={}",
                            request.userId(),
                            phone
                    );

                    twilioService.sendSMS(phone, message);
                }

                case VOICE -> {
                    log.info(
                            "Sending SOS VOICE call | userId={} | phone={}",
                            request.userId(),
                            phone
                    );

                    twilioService.makeCall(phone, message);
                }

                default -> {
                    log.warn(
                            "Unsupported notification channel={} | userId={}",
                            request.channel(),
                            request.userId()
                    );
                }
            }

            log.info(
                    "SOS notification processed successfully | userId={} | channel={}",
                    request.userId(),
                    request.channel()
            );

        } catch (Exception e) {

            log.error(
                    "SOS notification failed | userId={} | channel={} | phone={}. " +
                            "Exception will be propagated for Kafka retry/DLT.",
                    request.userId(),
                    request.channel(),
                    phone,
                    e
            );

            /*
             * IMPORTANT:
             *
             * Do NOT swallow this exception.
             *
             * @RetryableTopic needs the exception to escape the
             * Kafka listener so Spring Kafka can route the message
             * through the retry topics and eventually the DLT.
             */
            throw e;
        }
    }
}