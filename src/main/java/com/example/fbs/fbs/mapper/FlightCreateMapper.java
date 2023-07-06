package com.example.fbs.fbs.mapper;

import com.example.fbs.fbs.model.dto.FlightCreateDto;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import org.springframework.stereotype.Component;

@Component
public class FlightCreateMapper {

    public FlightCreateDto toDto(Flight flight) {
        return FlightCreateDto.builder()
                .departureAirport(flight.getDepartureAirport())
                .arrivalAirport(flight.getArrivalAirport())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .seats(flight.getSeats()).build();
    }

    public Flight toEntity(FlightCreateDto flightCreateDto) {
        return Flight.builder()
                .departureAirport(flightCreateDto.getDepartureAirport())
                .arrivalAirport(flightCreateDto.getArrivalAirport())
                .departureTime(flightCreateDto.getDepartureTime())
                .arrivalTime(flightCreateDto.getArrivalTime())
                .seats(flightCreateDto.getSeats()).build();
    }
}
