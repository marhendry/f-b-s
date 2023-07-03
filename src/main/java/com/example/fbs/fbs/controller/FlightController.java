package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.mapper.FlightMapper;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Tag(
        name = "Flight controller",
        description = "Controller to manipulate with flights in the App."
)
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/flights")
public class FlightController {

    private final FlightService flightService;

    private final FlightMapper flightMapper;

    private final JwtService jwtService;

    @Operation(summary = "create new Flight in the app")
    @PostMapping("/create")
    public ResponseEntity<FlightDto> createFlight(@RequestBody FlightDto flightDto) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        if (hasAdminAuthority(extractTokenFromRequest(request))) {
            Flight flight = flightService.createFlight(flightDto);
            flightDto = flightMapper.toDto(flight);
            return ResponseEntity.status(HttpStatus.CREATED).body(flightDto);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private boolean hasAdminAuthority(String token) {
        UserDetails userDetails = jwtService.extractUserDetails(token);
        return userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
    }

    @Operation(summary = "update existing Flight in the app")
    @PutMapping("/{flightId}")
    public ResponseEntity<FlightDto> updateFlight(
            @PathVariable Long flightId, @RequestBody FlightDto flightDto) {
        Flight updatedFlight = flightService.updateFlight(flightId, flightDto);
        FlightDto updatedFlightDto = flightMapper.toDto(updatedFlight);
        return ResponseEntity.ok(updatedFlightDto);
    }

    @Operation(summary = "delete existing Flight in the app")
    @DeleteMapping("/{flightId}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long flightId) {
        flightService.deleteFlight(flightId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "get existing Flight in the app by Id")
    @GetMapping("/{flightId}")
    public ResponseEntity<FlightDto> getFlightById(@PathVariable Long flightId) {
        Flight flight = flightService.getFlightById(flightId);
        FlightDto flightDto = flightMapper.toDto(flight);
        return ResponseEntity.ok(flightDto);
    }

    @Operation(summary = "get all Flights in the app")
    @GetMapping
    public ResponseEntity<List<FlightDto>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();
        List<FlightDto> flightDtos = flights.stream()
                .map(flightMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flightDtos);
    }
}