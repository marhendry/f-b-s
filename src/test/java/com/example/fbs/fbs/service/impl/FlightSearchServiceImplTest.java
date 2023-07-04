package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FlightSearchServiceImplTest {

    public static final int YEAR = 2023;

    public static final int MONTH = 7;

    public static final int DAY_OF_MONTH = 1;

    public static final int HOUR_TEN = 10;

    public static final int HOURTWELVE = 12;

    public static final int MINUTE_ZERO = 0;

    public static final String DEPARTURE_AIRPORTE_A = "A";

    public static final String ARRIVAL_AIRPORT_B = "B";

    private FlightSearchServiceImpl flightSearchService;

    @Mock
    private FlightRepository flightRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        flightSearchService = new FlightSearchServiceImpl(flightRepository);
    }

    @Test
    void searchFlights_HappyCase() {
        LocalDateTime startDateTime = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR_TEN, MINUTE_ZERO);
        LocalDateTime endDateTime = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOURTWELVE, MINUTE_ZERO);

        List<Flight> expectedFlights = new ArrayList<>();
        expectedFlights.add(new Flight());
        expectedFlights.add(new Flight());

        when(flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
                DEPARTURE_AIRPORTE_A, ARRIVAL_AIRPORT_B, startDateTime, endDateTime)).thenReturn(expectedFlights);

        List<Flight> flights = flightSearchService.searchFlights(DEPARTURE_AIRPORTE_A, ARRIVAL_AIRPORT_B, startDateTime, endDateTime);

        assertEquals(expectedFlights, flights);
        verify(flightRepository).findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
                DEPARTURE_AIRPORTE_A, ARRIVAL_AIRPORT_B, startDateTime, endDateTime);
    }

    @Test
    void searchFlights_NotValidSearchParams() {
        String departureAirport = DEPARTURE_AIRPORTE_A;
        String arrivalAirport = ARRIVAL_AIRPORT_B;
        LocalDateTime startDateTime = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR_TEN, MINUTE_ZERO);
        LocalDateTime endDateTime = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOURTWELVE, MINUTE_ZERO);

        when(flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
                departureAirport, arrivalAirport, startDateTime, endDateTime)).thenReturn(new ArrayList<>());

        List<Flight> flights = flightSearchService.searchFlights(departureAirport, arrivalAirport, startDateTime, endDateTime);

        assertEquals(MINUTE_ZERO, flights.size());
        verify(flightRepository).findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
                departureAirport, arrivalAirport, startDateTime, endDateTime);
    }
}