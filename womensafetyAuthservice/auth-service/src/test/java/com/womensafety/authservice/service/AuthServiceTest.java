package com.womensafety.authservice.service;

import com.womensafety.authservice.dto.AuthRequest;
import com.womensafety.authservice.dto.AuthResponse;
import com.womensafety.authservice.dto.RegisterRequest;
import com.womensafety.authservice.model.User;
import com.womensafety.authservice.model.VerificationToken;
import com.womensafety.authservice.repository.UserRepository;
import com.womensafety.authservice.repository.VerificationTokenRepository;
import com.womensafety.authservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;


    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
   // @Test
    void testRegisterUserSuccess() {
        // Arrange
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "pass123");
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("john");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setIsVerified(false);

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(savedUser.getUsername())).thenReturn("jwt-token");
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(new VerificationToken());

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("User registered successfully", response.getMessage());
       // verify(kafkaTemplate, times(2)).send(anyString(), any()); // one for EmailVerificationEvent, one for UserCreatedEvent
        verify(verificationTokenRepository, times(1)).save(any());
    }



//@Test
    void testLoginUserSuccess() {
        AuthRequest request = new AuthRequest("john", "pass123");
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("pass123");
        user.setRole("ROLE_USER");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("john");
        when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);// Remove and check
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "pass123")).thenReturn(true);//Remove and test exception
        when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails.getUsername())).thenReturn("jwt-token");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("Login successful", response.getMessage());
    }

    // Additional edge cases can be added like user already exists, invalid credentials, etc.
}
