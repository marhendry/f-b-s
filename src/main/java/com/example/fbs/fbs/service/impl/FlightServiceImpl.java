package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.exception.FlightNotFoundException;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.repository.FlightRepository;
import com.example.fbs.fbs.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    @Override
    public Flight createFlight(FlightDto flightDto) {
        Flight flight = Flight.from(flightDto);
        return flightRepository.save(flight);
    }

    @Override
    public Flight updateFlight(Long flightId, FlightDto flightDto) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found with id: " + flightId));
        updateFlightWithNewData(flightDto, flight);
        return flightRepository.save(flight);
    }

    @Override
    public void deleteFlight(Long flightId) {
        flightRepository.deleteById(flightId);
    }

    @Override
    public Flight getFlightById(Long flightId) {
        return flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found with id: " + flightId));
    }

    @Override
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    private static void updateFlightWithNewData(FlightDto flightDto, Flight flight) {
        flight.setDepartureAirport(flightDto.getDepartureAirport());
        flight.setArrivalAirport(flightDto.getArrivalAirport());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        flight.setSeats(flightDto.getSeats());
    }
}
