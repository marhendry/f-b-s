package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.mapper.FlightMapper;
import com.example.fbs.fbs.model.dto.FlightDto;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.model.entity.Role;
import com.example.fbs.fbs.model.entity.User;
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

    public static final long FLIGHT_ID_SECOND = 2L;

    public static final String AIRPORT_3 = "Airport3";

    public static final String AIRPORT_4 = "Airport4";

    public static final int DAY_OF_MONTH_2 = 2;

    @Mock
    private FlightService flightService;

    @Mock
    private FlightMapper flightMapper;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private FlightController flightController;

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
                        .post("/flights/create")
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
                        .post("/flights/create")
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
        Long flightId = FLIGHT_ID_FIRST;
        Flight flight = new Flight();
        flight.setId(flightId);
        flight.setDepartureAirport(AIRPORT_1);
        flight.setArrivalAirport(AIRPORT_2);
        flight.setDepartureTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR, MINUTE));
        flight.setArrivalTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR_2, MINUTE));
        flight.setSeats(SEATS_OF_FLIGHT);

        FlightDto flightDto = new FlightDto();
        flightDto.setId(flight.getId());
        flightDto.setDepartureAirport(flight.getDepartureAirport());
        flightDto.setArrivalAirport(flight.getArrivalAirport());
        flightDto.setDepartureTime(flight.getDepartureTime());
        flightDto.setArrivalTime(flight.getArrivalTime());
        flightDto.setSeats(flight.getSeats());

        when(flightService.getFlightById(flightId)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/flights/{flightId}", flightId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(flightDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.departureAirport").value(flightDto.getDepartureAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.arrivalAirport").value(flightDto.getArrivalAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.departureTime").value(flightDto.getDepartureTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.arrivalTime").value(flightDto.getArrivalTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.seats").value(flightDto.getSeats()));
    }

    @Test
    void testGetAllFlights_ReturnsFlightDtoList() throws Exception {
        Flight flight1 = new Flight();
        flight1.setId(FLIGHT_ID_FIRST);
        flight1.setDepartureAirport(AIRPORT_1);
        flight1.setArrivalAirport(AIRPORT_2);
        flight1.setDepartureTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR, MINUTE));
        flight1.setArrivalTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH, HOUR_2, MINUTE));
        flight1.setSeats(SEATS_OF_FLIGHT);

        Flight flight2 = new Flight();
        flight2.setId(FLIGHT_ID_SECOND);
        flight2.setDepartureAirport(AIRPORT_3);
        flight2.setArrivalAirport(AIRPORT_4);
        flight2.setDepartureTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH_2, HOUR, MINUTE));
        flight2.setArrivalTime(LocalDateTime.of(YEAR, MONTH, DAY_OF_MONTH_2, HOUR_2, MINUTE));
        flight2.setSeats(SEATS_OF_UPDATED_FLIGHT);

        List<Flight> flights = Arrays.asList(flight1, flight2);

        FlightDto flightDto1 = new FlightDto();
        flightDto1.setId(flight1.getId());
        flightDto1.setDepartureAirport(flight1.getDepartureAirport());
        flightDto1.setArrivalAirport(flight1.getArrivalAirport());
        flightDto1.setDepartureTime(flight1.getDepartureTime());
        flightDto1.setArrivalTime(flight1.getArrivalTime());
        flightDto1.setSeats(flight1.getSeats());

        FlightDto flightDto2 = new FlightDto();
        flightDto2.setId(flight2.getId());
        flightDto2.setDepartureAirport(flight2.getDepartureAirport());
        flightDto2.setArrivalAirport(flight2.getArrivalAirport());
        flightDto2.setDepartureTime(flight2.getDepartureTime());
        flightDto2.setArrivalTime(flight2.getArrivalTime());
        flightDto2.setSeats(flight2.getSeats());

        when(flightService.getAllFlights()).thenReturn(flights);
        when(flightMapper.toDto(flight1)).thenReturn(flightDto1);
        when(flightMapper.toDto(flight2)).thenReturn(flightDto2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/flights/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(flightDto1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].departureAirport").value(flightDto1.getDepartureAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].arrivalAirport").value(flightDto1.getArrivalAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].departureTime").value(flightDto1.getDepartureTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].arrivalTime").value(flightDto1.getArrivalTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].seats").value(flightDto1.getSeats()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(flightDto2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].departureAirport").value(flightDto2.getDepartureAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].arrivalAirport").value(flightDto2.getArrivalAirport()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].departureTime").value(flightDto2.getDepartureTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].arrivalTime").value(flightDto2.getArrivalTime().format(formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].seats").value(flightDto2.getSeats()));
    }
}