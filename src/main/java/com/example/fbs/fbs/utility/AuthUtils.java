package com.example.fbs.fbs.utility;

import com.example.fbs.fbs.config.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
public class AuthUtils {

    public static final int BEGIN_INDEX = 7;

    public static String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(BEGIN_INDEX);
        }
        return null;
    }

    public static boolean hasClientAuthority(JwtService jwtService, String token) {
        UserDetails userDetails = jwtService.extractUserDetails(token);
        return userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("CLIENT"));
    }

    public static boolean hasAdminAuthority(JwtService jwtService, String token) {
        UserDetails userDetails = jwtService.extractUserDetails(token);
        return userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }

}
