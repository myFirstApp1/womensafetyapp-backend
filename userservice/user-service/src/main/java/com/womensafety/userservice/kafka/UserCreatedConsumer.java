package com.womensafety.userservice.kafka;

import com.tl.womensafety.common.events.UserCreatedEvent;
import com.womensafety.userservice.model.UserProfile;
import com.womensafety.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedConsumer {

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
            topics = "${app.user.created.topic}",
            groupId = "user-service",
            containerFactory = "userCreatedEventKafkaListenerContainerFactory"
    )
    public void consume(UserCreatedEvent event) {

        log.info(
                "Received user.created event for userId: {}, phone: {}",
                event.userId(),
                event.phone()
        );

        userProfileRepository.findByUserId(event.userId())
                .ifPresentOrElse(
                        existing -> {
                            log.info(
                                    "Profile already exists for userId {}",
                                    event.userId()
                            );
                        },
                        () -> {

                            UserProfile profile = UserProfile.builder()
                                    .userId(event.userId())
                                    .name(event.userName())
                                    .email(event.email())
                                    .phone(event.phone())
                                    //.isVerified(event.isVerified())
                                    .build();

                            userProfileRepository.save(profile);

                            log.info(
                                    "Created new profile for userId {}, email {}, phone {}",
                                    event.userId(),
                                    profile.getEmail(),
                                    profile.getPhone()
                            );
                        }
                );
    }
}