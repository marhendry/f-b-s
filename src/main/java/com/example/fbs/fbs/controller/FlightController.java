package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.config.swagger.SwaggerProfileApiResponseStatusConfiguration;
import com.example.fbs.fbs.mapper.FlightMapper;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.fbs.fbs.utility.AuthUtils.extractTokenFromRequest;
import static com.example.fbs.fbs.utility.AuthUtils.hasAdminAuthority;

@Tag(
        name = "Flight controller",
        description = """
        Controller to manipulate with flights in the App.
        This controller allows administrators to create flights or routes based on the provided FlightDto.
        The operation is only available to logged-in administrators, who need to provide a JWT token
        generated during the login process. The JWT token should be copied and set in the Authorization header as a Bearer token.
                                            
        In addition to creating flights, administrators have the following capabilities:
                                            
        Delete a flight from the database.
        Retrieve flight information based on its ID.
        Get a list of all available flights in the database.
                            
        Controller also provide to search possible flights based on departure and arrival airports, and date in the App.
        The startDateTimeString and endDateTimeString parameters should be provided in the format "yyyy-MM-dd HH:mm".
        This means that the date should be in the format "yyyy-MM-dd" (year-month-day),
        followed by a space, and then the time in the format "HH:mm" (hours:minutes).
        For example, a valid input would be "2023-07-01 09:30" to represent July 1, 2023, at 09:30 AM."""
)
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/flights")
@SwaggerProfileApiResponseStatusConfiguration
public class FlightController {

    private final FlightService flightService;

    private final FlightMapper flightMapper;

    private final JwtService jwtService;

    @Operation(summary = "create new Flight in the app")
    @PostMapping()
    public ResponseEntity<FlightDto> createFlight(@RequestBody FlightDto flightDto) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        if (hasAdminAuthority(jwtService, extractTokenFromRequest(request))) {
            Flight flight = flightService.createFlight(flightDto);
            flightDto = flightMapper.toDto(flight);
            return ResponseEntity.status(HttpStatus.CREATED).body(flightDto);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
                .toList();
        return ResponseEntity.ok(flightDtos);
    }

    @Operation(summary = "Search flights")
    @GetMapping("/search-flights/")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam("departureAirport") String departureAirport,
            @RequestParam("arrivalAirport") String arrivalAirport,
            @RequestParam("startDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") String startDateTimeString,
            @RequestParam("endDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") String endDateTimeString) {

        LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        List<Flight> flights = flightService.searchFlights(departureAirport, arrivalAirport, startDateTime, endDateTime);
        if (flights.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(flights);
    }
}