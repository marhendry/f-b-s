package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.exception.FlightNotFoundException;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FlightServiceImplTest {

    public static final long FLIGHT_ID_ONE = 1L;

    public static final String DEPARTURE_AIRPORT_A = "A";

    public static final String ARRIVAL_AIRPORT_B = "B";

    public static final int YEAR = 2023;

    public static final int MONTH = 7;

    public static final int DAY_OF_MONTH = 1;

    public static final int HOUR_TEN = 10;

    public static final int HOUR_TWELVE = 12;

    public static final int MINUTE_ZERO = 0;

    public static final int SEATS_100 = 100;

    private FlightServiceImpl flightService;

    @Mock
    private FlightRepository flightRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        flightService = new FlightServiceImpl(flightRepository);
    }

    @Test
    void createFlight_HappyCase() {
        FlightDto flightDto = createFlightDto();
        Flight expectedFlight = createFlightFromDto(flightDto);

        when(flightRepository.save(any(Flight.class))).thenReturn(expectedFlight);

        Flight createdFlight = flightService.createFlight(flightDto);

        assertEquals(expectedFlight, createdFlight);
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void updateFlight_HappyCase_ExistingFlightId_ValidFlightDto() {
        FlightDto flightDto = createFlightDto();
        Flight existingFlight = createFlightFromDto(flightDto);

        when(flightRepository.findById(FLIGHT_ID_ONE)).thenReturn(Optional.of(existingFlight));
        when(flightRepository.save(any(Flight.class))).thenReturn(existingFlight);

        Flight updatedFlight = flightService.updateFlight(FLIGHT_ID_ONE, flightDto);

        assertEquals(existingFlight, updatedFlight);
        verify(flightRepository).findById(FLIGHT_ID_ONE);
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void updateFlight_NonExistingFlightId_FlightNotFoundException() {
        Long flightId = FLIGHT_ID_ONE;
        FlightDto flightDto = createFlightDto();

        when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class, () -> flightService.updateFlight(flightId, flightDto));
        verify(flightRepository).findById(flightId);
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void deleteFlight_HappyCase() {
        Long flightId = FLIGHT_ID_ONE;

        flightService.deleteFlight(flightId);

        verify(flightRepository).deleteById(flightId);
    }

    @Test
    void getFlightById_HappyCase() {
        Long flightId = FLIGHT_ID_ONE;
        Flight expectedFlight = createFlightFromDto(createFlightDto());

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(expectedFlight));

        Flight flight = flightService.getFlightById(flightId);

        assertEquals(expectedFlight, flight);
        verify(flightRepository).findById(flightId);
    }

    @Test
    void getFlightById_NonExistingFlightId_FlightNotFoundException() {
        Long flightId = FLIGHT_ID_ONE;

        when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class, () -> flightService.getFlightById(flightId));
        verify(flightRepository).findById(flightId);
    }

    @Test
    void getAllFlights_NoFlights_EmptyList() {
        when(flightRepository.findAll()).thenReturn(new ArrayList<>());

        List<Flight> flights = flightService.getAllFlights();

        assertTrue(flights.isEmpty());
        verify(flightRepository).findAll();
    }

    @Test
    void getAllFlights_HappyCase() {
        List<Flight> expectedFlights = createFlightList();

        when(flightRepository.findAll()).thenReturn(expectedFlights);

        List<Flight> flights = flightService.getAllFlights();

        assertEquals(expectedFlights, flights);
        verify(flightRepository).findAll();
    }

    private FlightDto createFlightDto() {
        return FlightDto.builder()
                .id(FLIGHT_ID_ONE)
                .departureAirport(DEPARTURE_AIRPORT_A)
                .arrivalAirport(ARRIVAL_AIRPORT_B)
                .departureTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR_TEN, MINUTE_ZERO))
                .arrivalTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR_TWELVE, MINUTE_ZERO))
                .seats(SEATS_100)
                .build();
    }

    private Flight createFlightFromDto(FlightDto flightDto) {
        Flight flight = new Flight();
        flight.setId(flightDto.getId());
        flight.setDepartureAirport(flightDto.getDepartureAirport());
        flight.setArrivalAirport(flightDto.getArrivalAirport());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        flight.setSeats(flightDto.getSeats());
        return flight;
    }

    private List<Flight> createFlightList() {
        List<Flight> flights = new ArrayList<>();
        flights.add(createFlightFromDto(createFlightDto()));
        flights.add(createFlightFromDto(createFlightDto()));
        return flights;
    }
}