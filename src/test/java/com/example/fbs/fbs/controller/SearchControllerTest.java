package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.repository.FlightRepository;
import com.example.fbs.fbs.service.impl.FlightSearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    public static final String AIRPORT_1 = "Airport1";

    public static final String AIRPORT_2 = "Airport2";

    public static final int YEAR = 2023;

    public static final int MONTH = 7;

    public static final int DAY_OF_MONTH_1 = 1;

    public static final int HOUR_TEN = 10;

    public static final int MINUTE_ZERO = 0;

    public static final int HOUR_TWELVE = 12;

    public static final long FLIGHT_ID_1 = 1L;

    public static final int FLIGHT_SEATS_100 = 100;

    public static final long FLIGHT_ID_2 = 2L;

    public static final int FLIGHT_SEATS_200 = 150;

    private MockMvc mockMvc;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightSearchServiceImpl flightSearchService;


    private SearchController searchController;

    @BeforeEach
    void setup() {
        searchController = new SearchController(flightSearchService);
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @Test
    void testSearchFlights_ReturnsFlights() {
        String departureAirport = AIRPORT_1;
        String arrivalAirport = AIRPORT_2;
        LocalDateTime startDateTime = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH_1, HOUR_TEN, MINUTE_ZERO);
        LocalDateTime endDateTime = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH_1, HOUR_TWELVE, MINUTE_ZERO);

        Flight flight1 = new Flight();
        flight1.setId(FLIGHT_ID_1);
        flight1.setDepartureAirport(departureAirport);
        flight1.setArrivalAirport(arrivalAirport);
        flight1.setDepartureTime(startDateTime);
        flight1.setArrivalTime(endDateTime);
        flight1.setSeats(FLIGHT_SEATS_100);

        Flight flight2 = new Flight();
        flight2.setId(FLIGHT_ID_2);
        flight2.setDepartureAirport(departureAirport);
        flight2.setArrivalAirport(arrivalAirport);
        flight2.setDepartureTime(startDateTime);
        flight2.setArrivalTime(endDateTime);
        flight2.setSeats(FLIGHT_SEATS_200);

        List<Flight> expectedFlights = Arrays.asList(flight1, flight2);

        when(flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
                departureAirport, arrivalAirport, startDateTime, endDateTime))
                .thenReturn(expectedFlights);

        List<Flight> actualFlights = flightSearchService.searchFlights(
                departureAirport, arrivalAirport, startDateTime, endDateTime);

        assertEquals(expectedFlights, actualFlights);
    }

    @Test
    void testSearchFlights_ReturnsEmptyList() {
        String departureAirport = AIRPORT_1;
        String arrivalAirport = AIRPORT_2;
        LocalDateTime startDateTime = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH_1, HOUR_TEN, MINUTE_ZERO);
        LocalDateTime endDateTime = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH_1, HOUR_TWELVE, MINUTE_ZERO);

        when(flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
                departureAirport, arrivalAirport, startDateTime, endDateTime))
                .thenReturn(Collections.emptyList());

        List<Flight> flights = flightSearchService.searchFlights(
                departureAirport, arrivalAirport, startDateTime, endDateTime);

        assertTrue(flights.isEmpty());
    }
}