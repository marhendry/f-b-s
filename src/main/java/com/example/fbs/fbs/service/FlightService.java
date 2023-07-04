package com.example.fbs.fbs.service;

import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightService {

    List<Flight> getAllFlights();

    Flight getFlightById(Long flightId);

    void deleteFlight(Long flightId);

    Flight updateFlight(Long flightId, FlightDto flightDto);

    Flight createFlight(FlightDto flightDto);

    List<Flight> searchFlights(String departureAirport, String arrivalAirport, LocalDateTime startDateTime, LocalDateTime endDateTime);
}