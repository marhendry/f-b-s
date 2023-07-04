package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.dto.UserUpdateRequestDto;
import com.example.fbs.fbs.model.entity.User;
import com.example.fbs.fbs.repository.UserRepository;
import com.example.fbs.fbs.utility.impl.PasswordEncoderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    public static final String TEST_EMAIL_EXAMPLE_COM = "test_email@example.com";

    public static final String PASSWORD = "1111";

    public static final String BCRYPT_PASSWORD = "$2a$10$aYM3Dj2rLNOgP4wQGokTb.y4EwReiWuQn68.SWx3PH0SVTZVEjW1G";

    public static final String NEW_EMAIL_EXAMPLE_COM = "newemail@example.com";

    public static final String NEW_USER_NAME = "New Name";

    public static final String JOHN_WALKER = "John Walker";

    public static final String JOHNWALKER_MAIL_EXAMPLE_COM = "johnwalker@example.com";

    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoderImpl passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void authenticate_HappyCase() {
        User user = new User();
        user.setPassword(BCRYPT_PASSWORD);

        when(userRepository.findByEmail(TEST_EMAIL_EXAMPLE_COM)).thenReturn(Optional.of(user));
        when(passwordEncoder.validatePassword(PASSWORD, user.getPassword())).thenReturn(true);

        boolean isAuthenticated = userService.authenticate(TEST_EMAIL_EXAMPLE_COM, PASSWORD);

        assertTrue(isAuthenticated);
        verify(userRepository).findByEmail(TEST_EMAIL_EXAMPLE_COM);
        verify(passwordEncoder).validatePassword(PASSWORD, user.getPassword());
    }

    @Test
    void authenticate_InvalidCredentials_UsernameNotFoundException() {

        when(userRepository.findByEmail(TEST_EMAIL_EXAMPLE_COM)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.authenticate(TEST_EMAIL_EXAMPLE_COM, PASSWORD));
        verify(userRepository).findByEmail(TEST_EMAIL_EXAMPLE_COM);
        verify(passwordEncoder, never()).validatePassword(anyString(), anyString());
    }

    @Test
    void updateUserByEmail_HappyCase() {
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto(NEW_USER_NAME, NEW_EMAIL_EXAMPLE_COM);
        User user = new User();

        when(userRepository.findByEmail(TEST_EMAIL_EXAMPLE_COM)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUserByEmail(TEST_EMAIL_EXAMPLE_COM, updateRequest);

        assertEquals(updateRequest.getName(), user.getName());
        assertEquals(updateRequest.getEmail(), user.getEmail());
        verify(userRepository).findByEmail(TEST_EMAIL_EXAMPLE_COM);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserByEmail_InvalidEmail_UsernameNotFoundException() {
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto(NEW_USER_NAME, NEW_EMAIL_EXAMPLE_COM);

        when(userRepository.findByEmail(TEST_EMAIL_EXAMPLE_COM)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.updateUserByEmail(TEST_EMAIL_EXAMPLE_COM, updateRequest));
        verify(userRepository).findByEmail(TEST_EMAIL_EXAMPLE_COM);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void saveInitialUserInfo_NewUserRequestDto_SavesUserAndReturnsUuid() {
        UserRequestDto registrationRequest = new UserRequestDto(JOHN_WALKER, JOHNWALKER_MAIL_EXAMPLE_COM, PASSWORD);
        User currentUser = new User();
        String uuid = UUID.randomUUID().toString();
        currentUser.setUuid(uuid);

        when(userRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(currentUser);

        String savedUuid = userService.saveInitialUserInfo(registrationRequest);

        assertEquals(uuid, savedUuid);
        verify(userRepository).findByEmail(registrationRequest.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void saveInitialUserInfo_ExistingUserRequestDto_SavesUserAndReturnsUuid() {
        UserRequestDto registrationRequest = new UserRequestDto(JOHN_WALKER, JOHNWALKER_MAIL_EXAMPLE_COM, PASSWORD);
        User currentUser = new User();
        String uuid = UUID.randomUUID().toString();
        currentUser.setUuid(uuid);

        when(userRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.of(currentUser));
        when(userRepository.save(currentUser)).thenReturn(currentUser);

        String savedUuid = userService.saveInitialUserInfo(registrationRequest);

        assertEquals(uuid, savedUuid);
        verify(userRepository).findByEmail(registrationRequest.getEmail());
        verify(userRepository).save(currentUser);
    }
}