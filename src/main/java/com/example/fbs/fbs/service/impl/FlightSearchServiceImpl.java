package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.repository.FlightRepository;
import com.example.fbs.fbs.service.FlightSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightSearchServiceImpl implements FlightSearchService {

    private final FlightRepository flightRepository;

    @Override
    public List<Flight> searchFlights(String departureAirport, String arrivalAirport, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
                departureAirport, arrivalAirport, startDateTime, endDateTime);
    }
}
