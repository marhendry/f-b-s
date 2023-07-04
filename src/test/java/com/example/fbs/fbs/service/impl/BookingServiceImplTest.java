package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.exception.NotEnoughSeatsException;
import com.example.fbs.fbs.exception.NotFoundException;
import com.example.fbs.fbs.model.entity.Booking;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.model.entity.User;
import com.example.fbs.fbs.repository.BookingRepository;
import com.example.fbs.fbs.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {

    public static final long FLIGHT_ID_ONE = 1L;

    public static final int SEAT_COUNT_THREE = 3;

    public static final int SEAT_COUNT_TEN = 10;

    public static final int SEATS_FIVE = 5;

    public static final int SEAT_NUMBER = 2;

    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingServiceImpl(bookingRepository, flightRepository);
    }

    @Test
    void bookFlight_HappyCase() {
        User user = mock(User.class);
        Flight flight = new Flight();
        flight.setId(FLIGHT_ID_ONE);
        flight.setSeats(SEATS_FIVE);

        when(flightRepository.findById(FLIGHT_ID_ONE)).thenReturn(Optional.of(flight));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setId(FLIGHT_ID_ONE);
            return savedBooking;
        });

        Booking booking = bookingService.bookFlight(FLIGHT_ID_ONE, user, SEAT_COUNT_THREE);

        assertNotNull(booking);
        assertEquals(FLIGHT_ID_ONE, booking.getId());
        assertEquals(user, booking.getUser());
        assertEquals(flight, booking.getFlight());
        assertEquals(SEAT_COUNT_THREE, booking.getSeatNumber());
        assertNotNull(booking.getBookingTime());
        assertEquals(SEAT_NUMBER, flight.getSeats());
        verify(flightRepository).findById(FLIGHT_ID_ONE);
        verify(bookingRepository).save(any(Booking.class));
        verify(flightRepository).save(flight);
    }

    @Test
    void bookFlight_NotValidFlightId_NotFoundException() {
        User user = mock(User.class);

        when(flightRepository.findById(FLIGHT_ID_ONE)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.bookFlight(FLIGHT_ID_ONE, user, SEAT_COUNT_THREE));
        verify(flightRepository).findById(FLIGHT_ID_ONE);
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void bookFlight_NoSeatsAvailable_NotEnoughSeatsException() {
        User user = mock(User.class);
        Flight flight = new Flight();
        flight.setId(FLIGHT_ID_ONE);
        flight.setSeats(SEATS_FIVE);

        when(flightRepository.findById(FLIGHT_ID_ONE)).thenReturn(Optional.of(flight));

        assertThrows(NotEnoughSeatsException.class, () -> bookingService.bookFlight(FLIGHT_ID_ONE, user, SEAT_COUNT_TEN));
        verify(flightRepository).findById(FLIGHT_ID_ONE);
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void getAllUserBookings_HappyCase() {
        User user = mock(User.class);
        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(new Booking());
        expectedBookings.add(new Booking());

        when(bookingRepository.findByUser(user)).thenReturn(expectedBookings);

        List<Booking> bookings = bookingService.getAllUserBookings(user);

        assertNotNull(bookings);
        assertEquals(expectedBookings, bookings);
        verify(bookingRepository).findByUser(user);
    }

    @Test
    void cancelBooking_HappyCase() {
        Long bookingId = FLIGHT_ID_ONE;
        Booking booking = new Booking();
        booking.setId(bookingId);
        Flight flight = new Flight();
        flight.setId(FLIGHT_ID_ONE);
        flight.setSeats(SEAT_COUNT_THREE);
        booking.setFlight(flight);
        booking.setSeatNumber(SEAT_NUMBER);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        bookingService.cancelBooking(bookingId);

        assertEquals(SEATS_FIVE, flight.getSeats());
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).delete(booking);
        verify(flightRepository).save(flight);
    }

    @Test
    void cancelBooking_NotValidBookingId_NotFoundException() {
        Long bookingId = FLIGHT_ID_ONE;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.cancelBooking(bookingId));
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository, never()).delete(any(Booking.class));
        verify(flightRepository, never()).save(any(Flight.class));
    }
}