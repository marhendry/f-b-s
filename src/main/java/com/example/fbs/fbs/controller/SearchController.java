package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.swagger.SwaggerProfileApiResponseStatusConfiguration;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.service.impl.FlightSearchServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Tag(
        name = "Search Flights controller",
        description = """
    Controller to search possible flights based on departure and arrival airports, and date in the App.
    The startDateTimeString and endDateTimeString parameters should be provided in the format "yyyy-MM-dd HH:mm".
    This means that the date should be in the format "yyyy-MM-dd" (year-month-day),
    followed by a space, and then the time in the format "HH:mm" (hours:minutes).
    For example, a valid input would be "2023-07-01 09:30" to represent July 1, 2023, at 09:30 AM.
    """
)
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/search-flights/")
@SwaggerProfileApiResponseStatusConfiguration
public class SearchController {

    private final FlightSearchServiceImpl flightSearchService;

    @Operation(summary = "Search flights")
    @GetMapping
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam("departureAirport") String departureAirport,
            @RequestParam("arrivalAirport") String arrivalAirport,
            @RequestParam("startDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") String startDateTimeString,
            @RequestParam("endDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") String endDateTimeString) {

        LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        List<Flight> flights = flightSearchService.searchFlights(departureAirport, arrivalAirport, startDateTime, endDateTime);
        if (flights.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(flights);
    }
}