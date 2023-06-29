package com.example.fbs.fbs.controller.impl;

import com.example.fbs.fbs.controller.FlightController;
import com.example.fbs.fbs.mapper.FlightMapper;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class FlightControllerImpl implements FlightController {

    private final FlightService flightService;

    private final FlightMapper flightMapper;

    @Override
    public ResponseEntity<FlightDto> createFlight(@RequestBody FlightDto flightDto) {
        Flight flight = flightService.createFlight(flightDto);
        FlightDto createdFlightDto = flightMapper.toDto(flight);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFlightDto);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlightDto> updateFlight(
            @PathVariable Long flightId, @RequestBody FlightDto flightDto) {
        Flight updatedFlight = flightService.updateFlight(flightId, flightDto);
        FlightDto updatedFlightDto = flightMapper.toDto(updatedFlight);
        return ResponseEntity.ok(updatedFlightDto);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long flightId) {
        flightService.deleteFlight(flightId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<FlightDto> getFlightById(@PathVariable Long flightId) {
        Flight flight = flightService.getFlightById(flightId);
        FlightDto flightDto = flightMapper.toDto(flight);
        return ResponseEntity.ok(flightDto);
    }

    @Override
    public ResponseEntity<List<FlightDto>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();
        List<FlightDto> flightDtos = flights.stream()
                .map(flightMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(flightDtos);
    }
}