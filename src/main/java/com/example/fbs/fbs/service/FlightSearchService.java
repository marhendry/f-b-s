package com.example.fbs.fbs.service;

import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FlightSearchService {

    List<Flight> searchFlights(String departureAirport, String arrivalAirport, LocalDateTime startDateTime, LocalDateTime endDateTime);
}