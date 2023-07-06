package com.example.fbs.fbs.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static com.example.fbs.fbs.model.entity.Role.ADMIN;
import static com.example.fbs.fbs.model.entity.Role.CLIENT;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final CorsConfigurationSource configurationSource;

    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .cors()
                .configurationSource(configurationSource)
                .and()
                .authorizeHttpRequests(auth -> auth

                        .antMatchers(HttpMethod.PUT, "/system/**").hasAuthority(ADMIN.name())
                        .antMatchers(HttpMethod.PUT, "/system/**").hasAuthority(CLIENT.name())
                        .antMatchers(HttpMethod.POST, "/flights/**").hasAuthority(ADMIN.name())
                        .antMatchers(HttpMethod.DELETE, "/flights/**").hasAuthority(ADMIN.name())
                        .antMatchers(HttpMethod.PUT, "/flights/**").hasAuthority(ADMIN.name())
                        .antMatchers(HttpMethod.POST, "/bookings/**").hasAuthority(CLIENT.name())
                        .antMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/system/**", "/search-flights/**").permitAll()
                        .anyRequest()
                        .authenticated())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        return http.build();
    }
}