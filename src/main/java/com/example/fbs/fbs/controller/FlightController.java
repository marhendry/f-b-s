package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.model.dto.FlightDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(
        name = "Flight controller",
        description = "Controller to manipulate with flights in the App."
)
@RequestMapping(value = "/flights/")
public interface FlightController {

    @Operation(summary = "create new Flight in the app")
    @PostMapping("/create")
    ResponseEntity<FlightDto> createFlight(@RequestBody FlightDto flightDto);

    @Operation(summary = "update existing Flight in the app")
    @PutMapping("/{flightId}")
    ResponseEntity<FlightDto> updateFlight(@PathVariable Long flightId, @RequestBody FlightDto flightDto);

    @Operation(summary = "delete existing Flight in the app")
    @DeleteMapping("/{flightId}")
    ResponseEntity<Void> deleteFlight(@PathVariable Long flightId);

    @Operation(summary = "get existing Flight in the app by Id")
    @GetMapping("/{flightId}")
    ResponseEntity<FlightDto> getFlightById(@PathVariable Long flightId);

    @Operation(summary = "get all Flights in the app")
    @GetMapping
    ResponseEntity<List<FlightDto>> getAllFlights();
}