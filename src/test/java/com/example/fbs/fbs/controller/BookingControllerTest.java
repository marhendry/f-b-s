package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.model.entity.Booking;
import com.example.fbs.fbs.model.entity.User;
import com.example.fbs.fbs.repository.UserRepository;
import com.example.fbs.fbs.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingControllerTest {

    public static final long FLIGHT_ID = 1L;

    public static final int SEAT_COUNT = 2;

    public static final String VALID_TOKEN = "validToken";

    public static final String EMAIL = "user@example.com";

    public static final String BEARER = "Bearer ";

    public static final String CLIENT_ROLE = "CLIENT";

    public static final String INVALID_TOKEN_STRING = "invalidToken";

    public static final String EMAIL_NON_CLIENT_EXAMPLE_COM = "non_client@example.com";

    public static final String NON_CLIENT_ROLE = "NON_CLIENT";

    @Mock
    private BookingServiceImpl bookingService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBookFlight_WithValidToken_ReturnsOkResponse() {
        User user = new User();
        Booking booking = new Booking();

        when(request.getHeader("Authorization")).thenReturn(BEARER + VALID_TOKEN);
        when(jwtService.extractEmailFromToken(VALID_TOKEN)).thenReturn(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(java.util.Optional.of(user));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenAnswer(invocation
                -> Collections.singletonList(new SimpleGrantedAuthority(CLIENT_ROLE)));

        when(jwtService.extractUserDetails(VALID_TOKEN)).thenReturn(userDetails);

        when(bookingService.bookFlight(FLIGHT_ID, user, SEAT_COUNT)).thenReturn(booking);

        ResponseEntity<?> response = bookingController.bookFlight(FLIGHT_ID, SEAT_COUNT);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(booking, response.getBody());
    }

    @Test
    void testBookFlight_WithInvalidToken_ReturnsForbiddenResponse() {

        when(request.getHeader("Authorization")).thenReturn(BEARER + INVALID_TOKEN_STRING);
        when(jwtService.extractEmailFromToken(INVALID_TOKEN_STRING)).thenReturn(EMAIL_NON_CLIENT_EXAMPLE_COM);
        when(userRepository.findByEmail(EMAIL_NON_CLIENT_EXAMPLE_COM)).thenReturn(Optional.of(new User()));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenAnswer(invocation
                -> Collections.singletonList(new SimpleGrantedAuthority(NON_CLIENT_ROLE)));
        when(jwtService.extractUserDetails(INVALID_TOKEN_STRING)).thenReturn(userDetails);

        ResponseEntity<?> response = bookingController.bookFlight(FLIGHT_ID, SEAT_COUNT);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Assertions.assertTrue(response.getBody() instanceof Booking);
    }

    @Test
    void testBookFlight_WithUnknownUser_ReturnsBadRequestResponse() {

        when(request.getHeader("Authorization")).thenReturn(BEARER + VALID_TOKEN);
        when(jwtService.extractEmailFromToken(VALID_TOKEN)).thenReturn(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(jwtService.extractUserDetails(VALID_TOKEN)).thenReturn(userDetails);

        ResponseEntity<Booking> response = bookingController.bookFlight(FLIGHT_ID, SEAT_COUNT);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void testGetAllUserBookings_AuthenticatedClientUser_ReturnsListOfBookings() {

        when(request.getHeader("Authorization")).thenReturn(BEARER + VALID_TOKEN);
        when(jwtService.extractEmailFromToken(VALID_TOKEN)).thenReturn(EMAIL);

        User user = new User();
        user.setEmail(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenAnswer(invocation
                -> Collections.singletonList(new SimpleGrantedAuthority(CLIENT_ROLE)));

        when(jwtService.extractUserDetails(VALID_TOKEN)).thenReturn(userDetails);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking());
        bookings.add(new Booking());

        when(bookingService.getAllUserBookings(user)).thenReturn(bookings);

        ResponseEntity<List<Booking>> response = bookingController.getAllUserBookings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
    }

    @Test
    void testGetAllUserBookings_UnauthenticatedUser_ReturnsForbiddenResponse() {
        when(request.getHeader("Authorization")).thenReturn(null);

        UserDetails userDetails = mock(UserDetails.class);
        when(jwtService.extractUserDetails(null)).thenReturn(userDetails);

        ResponseEntity<List<Booking>> response = bookingController.getAllUserBookings();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAllUserBookings_AuthenticatedNonClientUser_ReturnsForbiddenResponse() {

        when(request.getHeader("Authorization")).thenReturn(BEARER + VALID_TOKEN);
        when(jwtService.extractEmailFromToken(VALID_TOKEN)).thenReturn(EMAIL);

        User user = new User();
        user.setEmail(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenAnswer(invocation
                -> Collections.singletonList(new SimpleGrantedAuthority(NON_CLIENT_ROLE)));
        when(jwtService.extractUserDetails(VALID_TOKEN)).thenReturn(userDetails);

        ResponseEntity<List<Booking>> response = bookingController.getAllUserBookings();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCancelBooking_AuthenticatedClientUser_ReturnsOkResponse() {

        when(request.getHeader("Authorization")).thenReturn(BEARER + VALID_TOKEN);
        when(jwtService.extractEmailFromToken(VALID_TOKEN)).thenReturn(EMAIL);

        User user = new User();
        user.setEmail(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenAnswer(invocation
                -> Collections.singletonList(new SimpleGrantedAuthority(CLIENT_ROLE)));

        when(jwtService.extractUserDetails(VALID_TOKEN)).thenReturn(userDetails);

        ResponseEntity<?> response = bookingController.cancelBooking(FLIGHT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Booking cancelled successfully.", response.getBody());
    }

    @Test
    void testCancelBooking_UnauthenticatedUser_ReturnsForbiddenResponse() {
        when(request.getHeader("Authorization")).thenReturn(null);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(jwtService.extractUserDetails(null)).thenReturn(userDetails);

        ResponseEntity<String> response = bookingController.cancelBooking(FLIGHT_ID);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody());

    }
}