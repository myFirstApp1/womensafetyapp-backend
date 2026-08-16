package com.womensafety.userservice.config;

import com.tl.womensafety.common.events.UserCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, UserCreatedEvent> userCreatedEventConsumerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                groupId
        );

        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        /*
         * IMPORTANT:
         *
         * Do NOT configure VALUE_DESERIALIZER_CLASS_CONFIG here.
         *
         * We are supplying the JsonDeserializer instance explicitly below.
         */

        JsonDeserializer<UserCreatedEvent> deserializer =
                new JsonDeserializer<>(UserCreatedEvent.class);

        /*
         * The event comes from our shared common library.
         */
        deserializer.addTrustedPackages(
                "com.tl.womensafety.common.events"
        );

        /*
         * Keep Kafka type headers available.
         */
        deserializer.setRemoveTypeHeaders(false);

        /*
         * Use the configured UserCreatedEvent type.
         */
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "userCreatedEventKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent>
    userCreatedEventKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent>
                factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                userCreatedEventConsumerFactory()
        );

        return factory;
    }
}