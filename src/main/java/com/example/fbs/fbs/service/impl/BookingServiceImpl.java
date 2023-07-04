package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.exception.NotEnoughSeatsException;
import com.example.fbs.fbs.exception.NotFoundException;
import com.example.fbs.fbs.model.entity.Booking;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.model.entity.User;
import com.example.fbs.fbs.repository.BookingRepository;
import com.example.fbs.fbs.repository.FlightRepository;
import com.example.fbs.fbs.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final FlightRepository flightRepository;

    @Override
    @Transactional
    public Booking bookFlight(Long flightId, User user, int seatCount) {
        Flight bookedFlight = flightRepository.findById(flightId)
                .orElseThrow(() -> new NotFoundException("Flight not found with id: " + flightId));

        int availableSeats = bookedFlight.getSeats();
        if (seatCount > availableSeats) {
            throw new NotEnoughSeatsException("Not enough available seats on the flight.");
        }

        Booking booking = createBooking(user, seatCount, bookedFlight);
        bookedFlight.setSeats(availableSeats - seatCount);

        bookingRepository.save(booking);
        flightRepository.save(bookedFlight);

        log.info("Booking created for user: {}", user.getEmail());
        return booking;
    }

    private static Booking createBooking(User user, int seatCount, Flight bookedFlight) {
        return new Booking()
            .setUser(user)
            .setFlight(bookedFlight)
            .setSeatNumber(seatCount)
            .setBookingTime(LocalDateTime.now());
    }

    @Override
    public List<Booking> getAllUserBookings(User user) {
        return bookingRepository.findByUser(user);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        Flight flight = booking.getFlight();
        int seatCount = booking.getSeatNumber();

        int availableSeats = flight.getSeats();
        flight.setSeats(availableSeats + seatCount);

        flightRepository.save(flight);
        bookingRepository.delete(booking);

        log.info("Booking cancelled with id: {}", bookingId);
    }
}