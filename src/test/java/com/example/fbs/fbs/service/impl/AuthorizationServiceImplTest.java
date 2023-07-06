package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.exception.NotFoundException;
import com.example.fbs.fbs.model.dto.UserLoginRequestDto;
import com.example.fbs.fbs.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthorizationServiceImplTest {

    public static final String SOME_TOKEN = "some_token";

    public static final String TEST_EMAIL_EXAMPLE_COM = "test_email@example.com";

    public static final String PASSWORD = "password";

    public static final String SOME_USER = "some_user";

    public static final String INVALID_TOKEN = "invalid_token";

    private AuthorizationServiceImpl authorizationService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorizationService = new AuthorizationServiceImpl(jwtService, userDetailsService);
    }

    @Test
    void generateToken_HappyCase_ReturnsToken() {
        UserLoginRequestDto loginRequestDto = new UserLoginRequestDto(TEST_EMAIL_EXAMPLE_COM, PASSWORD);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(loginRequestDto.getEmail())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(SOME_TOKEN);

        String token = authorizationService.generateToken(loginRequestDto);

        assertNotNull(token);
        assertEquals(SOME_TOKEN, token);
        verify(userDetailsService).loadUserByUsername(loginRequestDto.getEmail());
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void generateToken_NotValidUserLogin_ReturnsNull() {
        UserLoginRequestDto loginRequestDto = new UserLoginRequestDto(null, PASSWORD);
        String email = loginRequestDto.getEmail();

        User user = new User();
        user.setEmail(email);
        user.setPassword(PASSWORD);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                SOME_USER, user.getPassword(), new ArrayList<>()
        );
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        when(jwtService.generateToken(userDetails)).thenReturn(SOME_TOKEN);

        String token = authorizationService.generateToken(loginRequestDto);

        assertNotNull(token);
        verify(userDetailsService).loadUserByUsername(email);
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void validateToken_HappyCase() {
        String token = SOME_TOKEN;
        UserDetails userDetails = mock(UserDetails.class);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);

        boolean isValid = authorizationService.validateToken(token, userDetails);

        assertTrue(isValid);
        verify(jwtService).validateToken(token, userDetails);
    }

    @Test
    void generateToken_UserDetailsService_ThrowsException() {
        UserLoginRequestDto loginRequestDto = new UserLoginRequestDto(TEST_EMAIL_EXAMPLE_COM, PASSWORD);
        when(userDetailsService.loadUserByUsername(loginRequestDto.getEmail()))
                .thenThrow(new NotFoundException("Failed to load user details"));

        assertThrows(NotFoundException.class, () -> authorizationService.generateToken(loginRequestDto));
        verify(userDetailsService).loadUserByUsername(loginRequestDto.getEmail());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void validateToken_NotValidToken() {
        String token = INVALID_TOKEN;
        UserDetails userDetails = mock(UserDetails.class);
        when(jwtService.validateToken(token, userDetails)).thenReturn(false);

        boolean isValid = authorizationService.validateToken(token, userDetails);

        assertFalse(isValid);
        verify(jwtService).validateToken(token, userDetails);
    }
}