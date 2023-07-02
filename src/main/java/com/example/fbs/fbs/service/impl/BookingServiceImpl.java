package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.model.entity.Booking;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.model.entity.User;
import com.example.fbs.fbs.repository.BookingRepository;
import com.example.fbs.fbs.repository.FlightRepository;
import com.example.fbs.fbs.repository.UserRepository;
import com.example.fbs.fbs.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final FlightRepository flightRepository;

    @Override
    public Booking bookFlight(Long flightId, User user, int seatCount) {
        Flight bookedFlight = flightRepository.findById(flightId)
                .orElseThrow(() -> new NotFoundException("Flight not found with id: " + flightId));

        int availableSeats = bookedFlight.getSeats();
        if (seatCount > availableSeats) {
            throw new RuntimeException("No available seats on the flight.");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(bookedFlight);
        booking.setSeatNumber(seatCount);
        booking.setBookingTime(LocalDateTime.now());

        bookedFlight.setSeats(availableSeats - seatCount);

        bookingRepository.save(booking);
        flightRepository.save(bookedFlight);

        return booking;
    }
}