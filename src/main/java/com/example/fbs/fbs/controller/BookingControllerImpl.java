package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.model.dto.BookingDto;
import com.example.fbs.fbs.model.entity.Booking;
import com.example.fbs.fbs.model.entity.User;
import com.example.fbs.fbs.repository.UserRepository;
import com.example.fbs.fbs.service.impl.BookingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Tag(
        name = "Booking controller",
        description = "Controller to manipulate with bookings in the App."
)
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/bookings/")
public class BookingControllerImpl {

    private final BookingServiceImpl bookingService;

    private final JwtService jwtService;

    private final HttpServletRequest request;

    private final UserRepository userRepository;


    @Operation(summary = "create new Booking in the app")
    @PostMapping("/create")
    public ResponseEntity<?> bookFlight(@RequestParam("flightId") Long flightId, @RequestParam("seatCount") int seatCount) {
        String token = extractTokenFromRequest(request);

        if (!hasClientAuthority(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        String email = jwtService.extractEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookFlight(flightId, seatCount, user);
    }

    @NotNull
    private ResponseEntity<?> bookFlight(Long flightId, int seatCount, User user) {
        try {
            Booking booking = bookingService.bookFlight(flightId, user, seatCount);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Get all bookings for the user")
    @GetMapping("/user-bookings")
    public ResponseEntity<List<Booking>> getAllUserBookings() {
        String token = extractTokenFromRequest(request);

        if (!hasClientAuthority(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        String email = jwtService.extractEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingService.getAllUserBookings(user);

        return ResponseEntity.ok().body(bookings);
    }

    @Operation(summary = "Cancel a booking")
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        String token = extractTokenFromRequest(request);

        if (!hasClientAuthority(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking cancelled successfully.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private boolean hasClientAuthority(String token) {
        UserDetails userDetails = jwtService.extractUserDetails(token);
        return userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("CLIENT"));
    }

}