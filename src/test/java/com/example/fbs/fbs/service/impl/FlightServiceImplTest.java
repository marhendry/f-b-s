package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.exception.NotFoundException;
import com.example.fbs.fbs.mapper.FlightCreateMapper;
import com.example.fbs.fbs.mapper.FlightMapper;
import com.example.fbs.fbs.model.dto.FlightCreateDto;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    public static final LocalDateTime END_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR_TWELVE, MINUTE_ZERO);

    public static final LocalDateTime START_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR_TEN, MINUTE_ZERO);

    private FlightServiceImpl flightService;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @Mock
    private FlightCreateMapper flightCreateMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        flightService = new FlightServiceImpl(flightRepository, flightCreateMapper);
    }

    @Test
    void createFlight_HappyCase() {
        FlightCreateDto flightCreateDto = createFlightFromCreationDto();
        Flight expectedFlight = createFlightFromCreationDto(flightCreateDto);

        when(flightCreateMapper.toEntity(any(FlightCreateDto.class))).thenReturn(expectedFlight);
        when(flightRepository.save(any(Flight.class))).thenReturn(expectedFlight);

        Flight createdFlight = flightService.createFlight(flightCreateDto);

        assertEquals(expectedFlight, createdFlight);
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void updateFlight_HappyCase_ExistingFlightId_ValidFlightDto() {
        FlightDto flightDto = createFlightDto();
        Flight existingFlight = createFlightFromDto(flightDto);

        when(flightRepository.findById(FLIGHT_ID_ONE)).thenReturn(Optional.of(existingFlight));
        when(flightRepository.save(any(Flight.class))).thenReturn(existingFlight);

        Flight updatedFlight = flightService.updateFlight(flightDto);

        assertEquals(existingFlight, updatedFlight);
        verify(flightRepository).findById(FLIGHT_ID_ONE);
        verify(flightRepository).save(any(Flight.class));
    }

    @Test
    void updateFlight_NonExistingFlightId_FlightNotFoundException() {
        FlightDto flightDto = createFlightDto();

        when(flightRepository.findById(flightDto.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> flightService.updateFlight(flightDto));
        verify(flightRepository).findById(flightDto.getId());
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void deleteFlight_HappyCase() {
        Long flightId = FLIGHT_ID_ONE;

        when(flightRepository.existsById(flightId)).thenReturn(true);

        flightService.deleteFlight(flightId);

        verify(flightRepository).deleteById(flightId);
    }

    @Test
    void deleteFlight_FlightNotFound() {
        Long flightId = FLIGHT_ID_ONE;

        when(flightRepository.existsById(flightId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> flightService.deleteFlight(flightId));

        verify(flightRepository, never()).deleteById(flightId);
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

        assertThrows(NotFoundException.class, () -> flightService.getFlightById(flightId));
        verify(flightRepository).findById(flightId);
    }

//    @Test
//    void getAllFlights_NoFlights_EmptyList() {
//        when(flightRepository.findAll()).thenReturn(new ArrayList<>());
//
//        List<Flight> flights = flightService.getAllFlights();
//
//        assertTrue(flights.isEmpty());
//        verify(flightRepository).findAll();
//    }
//
//    @Test
//    void getAllFlights_HappyCase() {
//        List<Flight> expectedFlights = createFlightList();
//
//        when(flightRepository.findAll()).thenReturn(expectedFlights);
//
//        List<Flight> flights = flightService.getAllFlights();
//
//        assertEquals(expectedFlights, flights);
//        verify(flightRepository).findAll();
//    }

    @Test
    void testSearchFlights_ReturnsFlightPage() {
        String departureAirport = "Airport1";
        String arrivalAirport = "Airport2";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 7, 1, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 7, 1, 12, 0);
        Pageable pageable = PageRequest.of(0, 10);

        List<Flight> expectedFlights = Arrays.asList(new Flight(), new Flight(), new Flight());
        Page<Flight> expectedFlightPage = new PageImpl<>(expectedFlights, pageable, 3);

        when(flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(departureAirport, arrivalAirport, startDateTime, endDateTime, pageable))
                .thenReturn(expectedFlightPage);

        Page<Flight> flightPage = flightService.searchFlights(departureAirport, arrivalAirport, startDateTime, endDateTime, pageable);

        assertEquals(expectedFlightPage, flightPage);
        assertEquals(expectedFlights, flightPage.getContent());
    }

    @Test
    void testSearchFlights_ReturnsEmptyPage() {
        String departureAirport = "Airport1";
        String arrivalAirport = "Airport2";
        LocalDateTime startDateTime = START_DATE_TIME;
        LocalDateTime endDateTime = END_DATE_TIME;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Flight> emptyFlightPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(departureAirport, arrivalAirport, startDateTime, endDateTime, pageable))
                .thenReturn(emptyFlightPage);

        Page<Flight> flightPage = flightService.searchFlights(departureAirport, arrivalAirport, startDateTime, endDateTime, pageable);

        assertTrue(flightPage.isEmpty());
        assertEquals(0, flightPage.getTotalElements());
    }

    private FlightCreateDto createFlightFromCreationDto() {
        return FlightCreateDto.builder()
                .departureAirport(DEPARTURE_AIRPORT_A)
                .arrivalAirport(ARRIVAL_AIRPORT_B)
                .departureTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR_TEN, MINUTE_ZERO))
                .arrivalTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR_TWELVE, MINUTE_ZERO))
                .seats(SEATS_100)
                .build();
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

    private Flight createFlightFromCreationDto(FlightCreateDto flightDto) {
        Flight flight = new Flight();
        flight.setDepartureAirport(flightDto.getDepartureAirport());
        flight.setArrivalAirport(flightDto.getArrivalAirport());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        flight.setSeats(flightDto.getSeats());
        return flight;
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