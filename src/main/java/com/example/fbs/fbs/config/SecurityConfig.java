package com.example.fbs.fbs.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//        private final JwtTokenFilter jwtAuthFilter;
//    private final FilterChainExceptionHandler exceptionHandlerFiler;
    private final CorsConfigurationSource configurationSource;
    private final AuthenticationEntryPoint authenticationEntryPoint;
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
                        .antMatchers("/actuator/**").permitAll()
                        .antMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/system/**").permitAll()
                        .antMatchers(HttpMethod.GET,  "/a-banking/promo-offer/").permitAll()
                        .antMatchers(HttpMethod.GET, "/a-banking/promo-offer/**").hasAuthority("MANAGER")
                        .antMatchers(HttpMethod.POST, "/a-banking/promo-offer/**").hasAuthority("MANAGER")
                        .anyRequest()
                        .authenticated())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                .addFilterBefore(exceptionHandlerFiler, LogoutFilter.class)
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }

}