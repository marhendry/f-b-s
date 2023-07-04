package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.mapper.FlightMapper;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.model.entity.Role;
import com.example.fbs.fbs.model.entity.User;
import com.example.fbs.fbs.repository.FlightRepository;
import com.example.fbs.fbs.service.FlightService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FlightControllerTest {

    public static final String ADMIN_TOKEN = "<admin_token>";

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final long FLIGHT_ID_FIRST = 1L;

    public static final String AIRPORT_1 = "Airport1";

    public static final String AIRPORT_2 = "Airport2";

    public static final int YEAR = 2023;

    public static final int MONTH = 7;

    public static final int DAY_OF_MONTH = 1;

    public static final int HOUR = 10;

    public static final int MINUTE = 0;

    public static final int HOUR_2 = 12;

    public static final String UPDATED_AIRPORT_1 = "UpdatedAirport1";

    public static final String UPDATED_AIRPORT_2 = "UpdatedAirport2";

    public static final int SEATS_OF_FLIGHT = 100;

    public static final int SEATS_OF_UPDATED_FLIGHT = 150;

    public static final int DAY_OF_MONTH_2 = 2;

    public static final int DAY_OF_MONTH_1 = 1;

    public static final int HOUR_TEN = 10;
    public static final int HOUR_NINE = 9;

    public static final int MINUTE_ZERO = 0;

    public static final int HOUR_TWELVE = 12;
    public static final int HOUR_THIRTEEN = 12;

    public static final LocalDateTime START_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH_1, HOUR_TEN, MINUTE_ZERO);

    public static final LocalDateTime END_DATE_TIME = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH_1, HOUR_TWELVE, MINUTE_ZERO);

    private static final LocalDateTime START_DATE_TIME_FOR_SEARCH = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH_1, HOUR_NINE, MINUTE_ZERO);

    private static final LocalDateTime END_DATE_TIME_FOR_SEARCH = LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH_1, HOUR_THIRTEEN, MINUTE_ZERO);

    public static final Flight FLIGHT_1 = Flight.builder()
            .id(FLIGHT_ID_FIRST)
            .departureAirport(AIRPORT_1)
            .arrivalAirport(AIRPORT_2)
            .departureTime(START_DATE_TIME)
            .arrivalTime(END_DATE_TIME)
            .seats(SEATS_OF_FLIGHT).build();

    public static final FlightDto FLIGHT_DTO_1 = FlightDto.builder()
            .id(FLIGHT_1.getId())
            .departureAirport(FLIGHT_1.getDepartureAirport())
            .arrivalAirport(FLIGHT_1.getArrivalAirport())
            .departureTime(FLIGHT_1.getDepartureTime())
            .arrivalTime(FLIGHT_1.getArrivalTime())
            .seats(FLIGHT_1.getSeats())
            .build();

    public static final long FLIGHT_ID_2 = 2L;

    public static final int FLIGHT_SEATS_200 = 150;

    public static final Flight FLIGHT_2 = Flight.builder()
            .id(FLIGHT_ID_2)
            .departureAirport(AIRPORT_1)
            .arrivalAirport(AIRPORT_2)
            .departureTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR, MINUTE))
            .arrivalTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR_2, MINUTE))
            .seats(FLIGHT_SEATS_200).build();

    public static final FlightDto FLIGHT_DTO_2 = FlightDto.builder()
            .id(FLIGHT_2.getId())
            .departureAirport(FLIGHT_2.getDepartureAirport())
            .arrivalAirport(FLIGHT_2.getArrivalAirport())
            .departureTime(FLIGHT_2.getDepartureTime())
            .arrivalTime(FLIGHT_2.getArrivalTime())
            .seats(FLIGHT_2.getSeats())
            .build();

    @Mock
    private FlightService flightService;

    @Mock
    private FlightMapper flightMapper;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private FlightController flightController;

    @Mock
    private FlightRepository flightRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(flightController).build();
    }

    @Test
    void testCreateFlightWithAdminAuthority() throws Exception {
        FlightDto flightDto = new FlightDto();
        when(jwtService.extractUserDetails(ADMIN_TOKEN)).thenReturn(createUserWithAdminAuthority());

        Flight flight = new Flight();
        when(flightService.createFlight(flightDto)).thenReturn(flight);
        FlightDto mappedFlightDto = new FlightDto();
        when(flightMapper.toDto(flight)).thenReturn(mappedFlightDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/flights")
                        .header("Authorization", "Bearer <admin_token>")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    private User createUserWithAdminAuthority() {
        User user = new User();
        user.setRole(Role.ADMIN);
        return user;
    }

    @Test
    void testCreateFlightWithoutValidToken_ReturnsForbiddenResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        UserDetails userDetails = createUserDetailsWithoutAuthorities();
        when(jwtService.extractUserDetails(null)).thenReturn(userDetails);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/flights")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private UserDetails createUserDetailsWithoutAuthorities() {
        return new org.springframework.security.core.userdetails.User(
                USERNAME,
                PASSWORD,
                Collections.emptyList()
        );
    }

    @Test
    void testUpdateFlight_ReturnsUpdatedFlightDto() throws Exception {
        Long flightId = FLIGHT_ID_FIRST;
        FlightDto flightDto = new FlightDto();
        flightDto.setDepartureAirport(AIRPORT_1);
        flightDto.setArrivalAirport(AIRPORT_2);
        flightDto.setDepartureTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR, MINUTE));
        flightDto.setArrivalTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR_2, MINUTE));
        flightDto.setSeats(SEATS_OF_FLIGHT);

        Flight updatedFlight = new Flight();
        FlightDto updatedFlightDto = new FlightDto();
        updatedFlightDto.setId(flightId);
        updatedFlightDto.setDepartureAirport(UPDATED_AIRPORT_1);
        updatedFlightDto.setArrivalAirport(UPDATED_AIRPORT_2);
        updatedFlightDto.setDepartureTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH_2, HOUR, MINUTE));
        updatedFlightDto.setArrivalTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH_2, HOUR_2, MINUTE));
        updatedFlightDto.setSeats(SEATS_OF_UPDATED_FLIGHT);

        when(flightService.updateFlight(flightId, flightDto)).thenReturn(updatedFlight);
        when(flightMapper.toDto(updatedFlight)).thenReturn(updatedFlightDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/flights/{flightId}", flightId)
                        .content(objectMapper.writeValueAsString(flightDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(updatedFlightDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.departureAirport").value(updatedFlightDto.getDepartureAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.arrivalAirport").value(updatedFlightDto.getArrivalAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.departureTime").value(updatedFlightDto.getDepartureTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.arrivalTime").value(updatedFlightDto.getArrivalTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seats").value(updatedFlightDto.getSeats()));
    }

    @Test
    void testDeleteFlight_ReturnsNoContent() throws Exception {
        Long flightId = FLIGHT_ID_FIRST;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/flights/{flightId}", flightId))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string(""));

        Mockito.verify(flightService).deleteFlight(flightId);
    }


    @Test
    void testGetFlightById_ReturnsFlightDto() throws Exception {

        when(flightService.getFlightById(FLIGHT_1.getId())).thenReturn(FLIGHT_1);
        when(flightMapper.toDto(FLIGHT_1)).thenReturn(FLIGHT_DTO_1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/flights/{flightId}", FLIGHT_1.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(FLIGHT_DTO_1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.departureAirport").value(FLIGHT_DTO_1.getDepartureAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.arrivalAirport").value(FLIGHT_DTO_1.getArrivalAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.departureTime").value(FLIGHT_DTO_1.getDepartureTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.arrivalTime").value(FLIGHT_DTO_1.getArrivalTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seats").value(FLIGHT_DTO_1.getSeats()));
    }

    @Test
    void testGetAllFlights_ReturnsFlightDtoList() throws Exception {

        List<Flight> flights = Arrays.asList(FLIGHT_1, FLIGHT_2);

        when(flightService.getAllFlights()).thenReturn(flights);
        when(flightMapper.toDto(FLIGHT_1)).thenReturn(FLIGHT_DTO_1);
        when(flightMapper.toDto(FLIGHT_2)).thenReturn(FLIGHT_DTO_2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/flights/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(FLIGHT_DTO_1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].departureAirport").value(FLIGHT_DTO_1.getDepartureAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].arrivalAirport").value(FLIGHT_DTO_1.getArrivalAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].departureTime").value(FLIGHT_DTO_1.getDepartureTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].arrivalTime").value(FLIGHT_DTO_1.getArrivalTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seats").value(FLIGHT_DTO_1.getSeats()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(FLIGHT_DTO_2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].departureAirport").value(FLIGHT_DTO_2.getDepartureAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].arrivalAirport").value(FLIGHT_DTO_2.getArrivalAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].departureTime").value(FLIGHT_DTO_2.getDepartureTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].arrivalTime").value(FLIGHT_DTO_2.getArrivalTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].seats").value(FLIGHT_DTO_2.getSeats()));
    }

//    @Test
//    void testSearchFlights_ReturnsFlights() {
//        List<Flight> expectedFlights = Arrays.asList(FLIGHT_1, FLIGHT_2);
//
//        when(flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
//                AIRPORT_1, AIRPORT_2, START_DATE_TIME, END_DATE_TIME))
//                .thenReturn(expectedFlights);
//
//        List<Flight> actualFlights = flightService.searchFlights(
//                AIRPORT_1, AIRPORT_2, START_DATE_TIME, END_DATE_TIME);
//
//        assertEquals(expectedFlights, actualFlights);
//    }

    @Test
    void testSearchFlights_ReturnsEmptyList() {

        when(flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
                AIRPORT_1, AIRPORT_2, START_DATE_TIME, END_DATE_TIME))
                .thenReturn(Collections.emptyList());

        List<Flight> flights = flightService.searchFlights(
                AIRPORT_1, AIRPORT_2, START_DATE_TIME, END_DATE_TIME);

        assertTrue(flights.isEmpty());
    }

}