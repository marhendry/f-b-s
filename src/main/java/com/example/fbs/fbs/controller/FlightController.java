package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.mapper.FlightCreateMapper;
import com.example.fbs.fbs.mapper.FlightMapper;
import com.example.fbs.fbs.model.dto.FlightCreateDto;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                Get a list of all available flights in the database.
                                    
                The controller provides endpoints for searching and retrieving flights based on departure and arrival
                airports, as well as date in the application. The startDateTimeString and endDateTimeString parameters
                should be provided in the format "yyyy-MM-dd HH:mm". This format consists of the date in "yyyy-MM-dd"
                format (year-month-day), followed by a space, and then the time in "HH:mm" format (hours:minutes).
                For example, a valid input would be "2023-07-01 09:30" to represent July 1, 2023, at 09:30 AM.
                These search endpoints are accessible for both CLIENT and ADMIN roles. They allow searching for flights
                either by providing a flight ID or by specifying the departure and arrival airportsalong with thedate range.
                Pagination is also supported, allowing you to specify the page number and the page size for the output.
                Please note that the specific endpoint URLs and additional request parameters may vary depending
                on your application's configuration."""
)
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/flights")
public class FlightController {

    private final FlightService flightService;

    private final FlightMapper flightMapper;
    private final FlightCreateMapper flightCreateMapper;

    private final JwtService jwtService;

    @Operation(summary = "create new Flight in the app" +
            "Dates should be provided in the format yyyy-MM-dd HH:mm")
    @PostMapping()
    public ResponseEntity<FlightCreateDto> createFlight(@RequestBody FlightCreateDto flightCreateDto) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        if (hasAdminAuthority(jwtService, extractTokenFromRequest(request))) {
            Flight flight = flightService.createFlight(flightCreateDto);
            flightCreateDto = flightCreateMapper.toDto(flight);
            return ResponseEntity.status(HttpStatus.CREATED).body(flightCreateDto);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @Operation(summary = "update existing Flight in the app" +
            "Dates should be provided in the format yyyy-MM-dd HH:mm")
    @PutMapping()
    public ResponseEntity<FlightDto> updateFlight(
            @RequestBody FlightDto flightDto) {
        Flight updatedFlight = flightService.updateFlight(flightDto);
        FlightDto updatedFlightDto = flightMapper.toDto(updatedFlight);
        return ResponseEntity.ok(updatedFlightDto);
    }

    @Operation(summary = "delete existing Flight in the app")
    @DeleteMapping("/{flightId}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long flightId) {
        flightService.deleteFlight(flightId);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Search Flights" +
            "Dates should be provided in the format yyyy-MM-dd HH:mm")
    @GetMapping("/search")
    public ResponseEntity<Page<FlightDto>> getFlights(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "departureAirport", required = false) String departureAirport,
            @RequestParam(value = "arrivalAirport", required = false) String arrivalAirport,
            @RequestParam(value = "startDateTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") String startDateTimeString,
            @RequestParam(value = "endDateTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") String endDateTimeString,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        if (id != null) {
            Flight flight = flightService.getFlightById(id);
            if (flight == null) {
                return ResponseEntity.noContent().build();
            }
            FlightDto flightDto = flightMapper.toDto(flight);
            return ResponseEntity.ok(new PageImpl<>(List.of(flightDto)));
        } else {
            LocalDateTime startDateTime = startDateTimeString != null ? LocalDateTime.parse(startDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null;
            LocalDateTime endDateTime = endDateTimeString != null ? LocalDateTime.parse(endDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null;

            Pageable pageable = PageRequest.of(page, size);
            Page<Flight> flightPage = flightService.searchFlights(departureAirport, arrivalAirport, startDateTime, endDateTime, pageable);

            if (flightPage.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<FlightDto> flightDtos = flightPage.getContent().stream()
                    .map(flightMapper::toDto)
                    .toList();

            return ResponseEntity.ok(new PageImpl<>(flightDtos, pageable, flightPage.getTotalElements()));
        }
    }

    @Operation(summary = "Get all flights")
    @GetMapping()
    public ResponseEntity<Page<Flight>> getAllFlights(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Flight> flights = flightService.getAllFlights(pageable);
        return ResponseEntity.ok(flights);
    }

}