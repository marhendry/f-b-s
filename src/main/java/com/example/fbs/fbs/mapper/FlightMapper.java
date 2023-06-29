package com.example.fbs.fbs.mapper;

import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper {

    public FlightDto toDto(Flight flight) {
        FlightDto flightDto = new FlightDto();
        flightDto.setId(flight.getId());
        flightDto.setDepartureAirport(flight.getDepartureAirport());
        flightDto.setArrivalAirport(flight.getArrivalAirport());
        flightDto.setDepartureTime(flight.getDepartureTime());
        flightDto.setArrivalTime(flight.getArrivalTime());
        flightDto.setSeats(flight.getSeats());
        return flightDto;
    }

    public Flight toEntity(FlightDto flightDto) {
        Flight flight = new Flight();
        flight.setId(flightDto.getId());
        flight.setDepartureAirport(flightDto.getDepartureAirport());
        flight.setArrivalAirport(flightDto.getArrivalAirport());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        flight.setSeats(flightDto.getSeats());
        return flight;
    }
}
