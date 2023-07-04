package com.example.fbs.fbs.mapper;

import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper {

    public FlightDto toDto(Flight flight) {
        return FlightDto.builder()
                .id(flight.getId())
                .departureAirport(flight.getDepartureAirport())
                .arrivalAirport(flight.getArrivalAirport())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .seats(flight.getSeats()).build();

    }

    public Flight toEntity(FlightDto flightDto) {
        return Flight.builder()
                .id(flightDto.getId())
                .departureAirport(flightDto.getDepartureAirport())
                .arrivalAirport(flightDto.getArrivalAirport())
                .departureTime(flightDto.getDepartureTime())
                .arrivalTime(flightDto.getArrivalTime())
                .seats(flightDto.getSeats()).build();
    }
}
