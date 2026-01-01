package com.womensafety.userservice.kafka;


import com.tl.womensafety.common.events.UserCreatedEvent;
import com.womensafety.userservice.model.UserProfile;
import com.womensafety.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedConsumer {

    private final UserProfileRepository userProfileRepository;

    @KafkaListener(
            topics = "${app.verification.topic}",
            groupId = "user-service",
            containerFactory = "userCreatedEventKafkaListenerContainerFactory"
    )
    public void consume(UserCreatedEvent event) {
        log.info("Received user.created event for userId: {}", event.userId());

        // Check if profile already exists
        userProfileRepository.findByUserId(event.userId())
                .ifPresentOrElse(
                        existing -> log.info("Profile already exists for userId {}", event.userId()),
                        () -> {
                            UserProfile profile = UserProfile.builder()
                                    .userId(event.userId())
                                    .name(event.userName()) // optional: pre-fill username as name
                                    .email(event.email())
                                    .isVerified(event.isVerified()) // can store if you added this field
                                    .build();
                            userProfileRepository.save(profile);
                            log.info("Created new profile for userId {}, email {}", event.userId(),profile.getEmail());
                        }
                );
    }
}