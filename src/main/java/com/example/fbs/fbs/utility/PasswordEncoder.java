package com.example.fbs.fbs.utility;

public interface PasswordEncoder {

    String encode(String password);

    boolean validatePassword(String currentPassword, String encodedPassword);
}
