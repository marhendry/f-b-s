package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.exception.FlightNotFoundException;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.repository.FlightRepository;
import com.example.fbs.fbs.service.FlightSearchService;
import com.example.fbs.fbs.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightSearchServiceImpl implements FlightSearchService {

    private final FlightRepository flightRepository;

    @Override
    public List<Flight> searchFlights(String departureAirport, String arrivalAirport, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
                departureAirport, arrivalAirport, startDateTime, endDateTime);
    }
}
