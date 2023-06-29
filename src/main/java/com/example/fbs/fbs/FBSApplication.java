package com.example.fbs.fbs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Flight Booking System App",
		version = "2.0",
		description = "FlightBookingSystemApplication"))
@SecurityScheme(name = "FBSApp",
		type = SecuritySchemeType.HTTP,
		scheme = "basic")
@SpringBootApplication
public class FBSApplication {

	public static void main(String[] args) {
		SpringApplication.run(FBSApplication.class, args);
	}

}
