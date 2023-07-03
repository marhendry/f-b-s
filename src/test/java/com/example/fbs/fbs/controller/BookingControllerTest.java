package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.model.entity.Booking;
import com.example.fbs.fbs.model.entity.User;
import com.example.fbs.fbs.repository.UserRepository;
import com.example.fbs.fbs.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.webjars.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
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
        // given
        User user = new User();
        Booking booking = new Booking();

        when(request.getHeader("Authorization")).thenReturn(BEARER + VALID_TOKEN);
        when(jwtService.extractEmailFromToken(VALID_TOKEN)).thenReturn(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(java.util.Optional.of(user));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenAnswer(invocation -> {
            return Collections.singletonList(new SimpleGrantedAuthority(CLIENT_ROLE));
        });

        when(jwtService.extractUserDetails(VALID_TOKEN)).thenReturn(userDetails);

        when(bookingService.bookFlight(FLIGHT_ID, user, SEAT_COUNT)).thenReturn(booking);

        // when
        ResponseEntity<?> response = bookingController.bookFlight(FLIGHT_ID, SEAT_COUNT);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(booking, response.getBody());
    }

    @Test
    void testBookFlight_WithInvalidToken_ReturnsForbiddenResponse() {
        Long flightId = FLIGHT_ID;
        int seatCount = SEAT_COUNT;
        String token = INVALID_TOKEN_STRING;
        String email = EMAIL_NON_CLIENT_EXAMPLE_COM;

        when(request.getHeader("Authorization")).thenReturn(BEARER + token);
        when(jwtService.extractEmailFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenAnswer(invocation -> {
            return Collections.singletonList(new SimpleGrantedAuthority(NON_CLIENT_ROLE));
        });
        when(jwtService.extractUserDetails(token)).thenReturn(userDetails);

        ResponseEntity<?> response = bookingController.bookFlight(flightId, seatCount);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody());
    }

    @Test
    void testBookFlight_WithUnknownUser_ReturnsBadRequestResponse() {
        Long flightId = FLIGHT_ID;
        int seatCount = SEAT_COUNT;
        String token = VALID_TOKEN;
        String email = EMAIL;

        when(request.getHeader("Authorization")).thenReturn(BEARER + token);
        when(jwtService.extractEmailFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(jwtService.extractUserDetails(token)).thenReturn(userDetails);

        ResponseEntity<?> response = bookingController.bookFlight(flightId, seatCount);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody());
    }


    @Test
    void testGetAllUserBookings_AuthenticatedClientUser_ReturnsListOfBookings() {
        String token = VALID_TOKEN;
        String email = EMAIL;

        when(request.getHeader("Authorization")).thenReturn(BEARER + token);
        when(jwtService.extractEmailFromToken(token)).thenReturn(email);

        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenAnswer(invocation -> {
            return Collections.singletonList(new SimpleGrantedAuthority(CLIENT_ROLE));
        });

        when(jwtService.extractUserDetails(token)).thenReturn(userDetails);

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
        String token = VALID_TOKEN;
        String email = EMAIL;

        when(request.getHeader("Authorization")).thenReturn(BEARER + token);
        when(jwtService.extractEmailFromToken(token)).thenReturn(email);

        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenAnswer(invocation -> {
            return Collections.singletonList(new SimpleGrantedAuthority(NON_CLIENT_ROLE));
        });
        when(jwtService.extractUserDetails(token)).thenReturn(userDetails);

        ResponseEntity<List<Booking>> response = bookingController.getAllUserBookings();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCancelBooking_AuthenticatedClientUser_ReturnsOkResponse() {
        Long bookingId = FLIGHT_ID;
        String token = VALID_TOKEN;
        String email = EMAIL;

        when(request.getHeader("Authorization")).thenReturn(BEARER + token);
        when(jwtService.extractEmailFromToken(token)).thenReturn(email);

        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenAnswer(invocation -> {
            return Collections.singletonList(new SimpleGrantedAuthority(CLIENT_ROLE));
        });

        when(jwtService.extractUserDetails(token)).thenReturn(userDetails);

        ResponseEntity<?> response = bookingController.cancelBooking(bookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Booking cancelled successfully.", response.getBody());
    }

    @Test
    void testCancelBooking_UnauthenticatedUser_ReturnsForbiddenResponse() {
        Long bookingId = FLIGHT_ID;

        when(request.getHeader("Authorization")).thenReturn(null);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(jwtService.extractUserDetails(null)).thenReturn(userDetails);

        ResponseEntity response = bookingController.cancelBooking(bookingId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody());

    }

    @Test
    void testCancelBooking_UnknownBooking_ReturnsNotFoundResponse() {
        Long bookingId = FLIGHT_ID;
        String token = VALID_TOKEN;
        String email = EMAIL;

        when(request.getHeader("Authorization")).thenReturn(BEARER + token);
        when(jwtService.extractEmailFromToken(token)).thenReturn(email);

        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenAnswer(invocation -> {
            return Collections.singletonList(new SimpleGrantedAuthority(CLIENT_ROLE));
        });
        when(jwtService.extractUserDetails(token)).thenReturn(userDetails);

        doThrow(new NotFoundException("Booking not found with id: " + bookingId))
                .when(bookingService).cancelBooking(bookingId);

        ResponseEntity<?> response = bookingController.cancelBooking(bookingId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Booking not found with id: " + bookingId, response.getBody());
    }
}