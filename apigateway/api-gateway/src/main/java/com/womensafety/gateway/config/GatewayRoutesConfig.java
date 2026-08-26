package com.womensafety.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Value("${gateway.auth.url}")
    private String authServiceUrl;

    @Value("${gateway.user.url}")
    private String userServiceUrl;

    @Value("${gateway.sos.url}")
    private String sosServiceUrl;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {

        return builder.routes()

                // =====================================================
                // AUTH SERVICE
                // =====================================================

                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri(authServiceUrl))

                // =====================================================
                // USER SERVICE
                // =====================================================

                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri(userServiceUrl))

                // =====================================================
                // SOS SERVICE
                // =====================================================

                .route("sos-service", r -> r
                        .path(
                                "/api/sos/**",
                                "/api/device/**",
                                "/api/heartbeat/**",
                                "/api/tracking/**",
                                "/api/incidents/**",
                                "/api/family/**",
                                "/api/police/**",
                                "/api/ack/**",
                                "/api/evidence/**",
                                "/api/v1/ai/**"
                        )
                        .uri(sosServiceUrl))

                .build();
    }
}