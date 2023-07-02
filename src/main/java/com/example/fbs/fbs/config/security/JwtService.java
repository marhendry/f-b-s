package com.example.fbs.fbs.config.security;

import com.example.fbs.fbs.model.entity.Role;
import com.example.fbs.fbs.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtService {

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
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        Date currentTime = new Date();
        return currentTime.after(expiration);
    }

    public String generateToken(UserDetails userDetails) {
        String username = userDetails.getUsername();
        String role = userDetailsService.loadUserByUsername(username).getAuthorities().stream().findFirst().get().toString();
        String email = userDetails.getUsername();

        return createToken(username, role, email);
    }

    private String createToken(String username, String role, String email) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + jwtExpiration * 3600000);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", role)
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Authentication getAuthentication(String token) {
        Claims claims = extractAllClaims(token);
        String username = claims.getSubject();
        String role = (String) claims.get("roles");

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    public UserDetails extractUserDetails(String token) {
        String username = extractUsername(token);
        Claims claims = extractAllClaims(token);
        String role = (String) claims.get("roles");

        return User.builder().role(Role.from(role)).build();
    }

    public String extractEmailFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("email");
    }
}