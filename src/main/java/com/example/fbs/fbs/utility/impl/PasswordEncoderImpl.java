package com.example.fbs.fbs.utility.impl;

import com.example.fbs.fbs.utility.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordEncoderImpl implements PasswordEncoder {

    private static final int STRENGTH = 10;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(STRENGTH, new SecureRandom());

    @Override
    public String encode(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    @Override
    public boolean validatePassword(String currentPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(currentPassword, encodedPassword);
    }
}