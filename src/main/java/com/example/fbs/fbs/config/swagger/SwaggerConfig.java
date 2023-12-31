package com.example.fbs.fbs.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("flight-booking-system.api")
                .pathsToMatch("/flights/**", "/system/**", "/bookings/**", "/search-flights/**")
                .packagesToScan("com.example.fbs.fbs.controller")
                .build();
    }

    @Bean
    public OpenAPI flightBookingOpenAPI() {
        final String securitySchemeName = "JWT";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info().title("Flight Booking System API")
                        .description("API for Flight Booking System")
                        .version("1.0"));
    }

}
