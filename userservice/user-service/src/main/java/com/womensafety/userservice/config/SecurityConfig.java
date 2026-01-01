package com.womensafety.userservice.config;

import com.womensafety.userservice.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

   // @Bean
   // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       // http
               // .csrf(csrf -> csrf.disable()) // disable CSRF for APIs
              //  .authorizeHttpRequests(auth -> auth
                       // .requestMatchers("/api/users/verify/**").permitAll() // email verification is public
                        //.anyRequest().authenticated() // everything else needs authentication
               // )
               // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
       // return http.build();
   // }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() //  allow all endpoints for MVP
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

}
