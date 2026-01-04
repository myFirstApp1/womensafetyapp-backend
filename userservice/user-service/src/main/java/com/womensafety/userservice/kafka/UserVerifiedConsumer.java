package com.womensafety.userservice.kafka;

import com.tl.womensafety.common.events.UserVerifiedEvent;
import com.womensafety.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserVerifiedConsumer {

    private final UserProfileRepository userProfileRepository;
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = "${app.user.verified.topic}",
            groupId = "user-service"
    )
    public void consume(UserVerifiedEvent event) {

        log.info("Received USER_VERIFIED event for userId {}", event.getUserId());

        userProfileRepository.findByUserId(event.getUserId())
                .ifPresent(profile -> {
                    profile.setVerified(true);
                    userProfileRepository.save(profile);
                    log.info("User profile verified for userId {}", event.getUserId());
                });
    }
}