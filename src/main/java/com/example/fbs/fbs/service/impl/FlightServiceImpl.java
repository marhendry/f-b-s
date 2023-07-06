package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.exception.NotFoundException;
import com.example.fbs.fbs.mapper.FlightMapper;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.repository.FlightRepository;
import com.example.fbs.fbs.service.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    @Override
    public Flight createFlight(FlightDto flightDto) {
        Flight flight = flightMapper.toEntity(flightDto);
        log.info("Creating new flight: {}", flight);
        return flightRepository.save(flight);
    }

    @Override
    public Flight updateFlight(FlightDto flightDto) {
        Flight flight = flightRepository.findById(flightDto.getId())
                .orElseThrow(() -> new NotFoundException("Flight not found with id: " + flightDto.getId()));
        updateFlightWithNewData(flightDto, flight);
        log.info("Updating flight with id {}: {}", flightDto.getId(), flight);
        return flightRepository.save(flight);
    }

    @Override
    public void deleteFlight(Long flightId) {
        log.info("Deleting flight with id: {}", flightId);
        if (flightRepository.existsById(flightId)) {
            flightRepository.deleteById(flightId);
        } else {
            throw new NotFoundException("Flight not found with id: " + flightId);
        }
    }

    @Override
    public Flight getFlightById(Long flightId) {
        log.info("Retrieving flight with id: {}", flightId);
        return flightRepository.findById(flightId)
                .orElseThrow(() -> new NotFoundException("Flight not found with id: " + flightId));
    }

    @Override
    public Page<Flight> getAllFlights(Pageable pageable) {
        log.info("Retrieving all flights");
        return flightRepository.findAll(pageable);
    }

    @Override
    public Page<Flight> searchFlights(String departureAirport, String arrivalAirport, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        return flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
                departureAirport, arrivalAirport, startDateTime, endDateTime, pageable);
    }

    private static void updateFlightWithNewData(FlightDto flightDto, Flight flight) {
        flight.setDepartureAirport(flightDto.getDepartureAirport());
        flight.setArrivalAirport(flightDto.getArrivalAirport());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        flight.setSeats(flightDto.getSeats());
    }
}
