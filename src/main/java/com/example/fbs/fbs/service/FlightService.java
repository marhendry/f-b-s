package com.example.fbs.fbs.service;

import com.example.fbs.fbs.model.dto.FlightCreateDto;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightService {

    Page<Flight> getAllFlights(Pageable pageable);

    Flight getFlightById(Long flightId);

    void deleteFlight(Long flightId);

    Flight updateFlight(FlightDto flightDto);

    Flight createFlight(FlightCreateDto flightCreateDto);

    Page<Flight> searchFlights(String departureAirport, String arrivalAirport,
                               LocalDateTime startDateTime, LocalDateTime endDateTime,
                               Pageable pageable);
}