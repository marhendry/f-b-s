package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.dto.UserUpdateRequestDto;
import com.example.fbs.fbs.model.entity.Role;
import com.example.fbs.fbs.model.entity.User;
import com.example.fbs.fbs.repository.UserRepository;
import com.example.fbs.fbs.service.UserService;
import com.example.fbs.fbs.utility.impl.PasswordEncoderImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoderImpl passwordEncoder;


    @Override
    public boolean authenticate(String email, String password) {
        log.info("Authenticating user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String encodedPassword = user.getPassword();
        return passwordEncoder.validatePassword(password, encodedPassword);
    }

    @Override
    public void updateUserByUuid(String uuid, UserUpdateRequestDto updateRequest) {
        log.info("Updating user with email: {}", uuid);
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        updateUserData(updateRequest, user);
        userRepository.save(user);
    }

    @Override
    public String saveInitialUserInfo(UserRequestDto registrationRequest) {
        log.info("Saving initial user info for email: {}", registrationRequest.getEmail());
        User currentUser = userRepository.findByEmail(registrationRequest.getEmail())
                .orElseGet(() -> buildClient(registrationRequest));

        encodePassword(registrationRequest, currentUser);
        return userRepository.save(currentUser).getUuid();

    }

    private static void updateUserData(UserUpdateRequestDto updateRequest, User user) {
        user.setName(updateRequest.getName());
        user.setEmail(updateRequest.getUuid());
    }

    private void encodePassword(UserRequestDto registrationRequest, User currentUser) {
        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        currentUser.setPassword(encodedPassword);
    }

    private User buildClient(UserRequestDto registrationRequest) {
        return User.from(registrationRequest, UUID.randomUUID().toString(), Role.CLIENT);
    }

    @Override
    public String saveInitialAdminInfo(UserRequestDto registrationRequest) {
        log.info("Saving initial admin info for email: {}", registrationRequest.getEmail());
        User currentUser = userRepository.findByEmail(registrationRequest.getEmail())
                .orElseGet(() -> buildAdmin(registrationRequest));

        encodePassword(registrationRequest, currentUser);
        return userRepository.save(currentUser).getUuid();
    }

    private User buildAdmin(UserRequestDto registrationRequest) {
        return User.from(registrationRequest, UUID.randomUUID().toString(), Role.ADMIN);
    }
}