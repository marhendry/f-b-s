package com.example.fbs.fbs.config.security;

import com.example.fbs.fbs.model.entity.Role;
import com.example.fbs.fbs.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtService {

    public static final String ROLES = "roles";

    public static final String EMAIL = "email";

    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.secret.key.hex}")
    private String jwtSecret;

    @Value("${jwt.expiration.time.hours}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        Date currentTime = new Date();
        return currentTime.after(expiration);
    }

    public String generateToken(UserDetails userDetails) {
        String username = userDetails.getUsername();
        String role = userDetailsService.loadUserByUsername(username)
                .getAuthorities()
                .stream()
                .findFirst()
                .map(Objects::toString)
                .orElse(null);
        String email = userDetails.getUsername();

        return createToken(username, role, email);
    }

    private String createToken(String username, String role, String email) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + jwtExpiration * 3600000);

        return Jwts.builder()
                .setSubject(username)
                .claim(ROLES, role)
                .claim(EMAIL, email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Authentication getAuthentication(String token) {
        Claims claims = extractAllClaims(token);
        String username = claims.getSubject();
        String role = (String) claims.get(ROLES);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    public UserDetails extractUserDetails(String token) {
        Claims claims = extractAllClaims(token);
        String role = (String) claims.get(ROLES);

        return User.builder().role(Role.from(role)).build();
    }

    public String extractEmailFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get(EMAIL);
    }
}