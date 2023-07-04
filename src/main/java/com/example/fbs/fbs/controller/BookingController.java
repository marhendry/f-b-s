package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.config.swagger.SwaggerProfileApiResponseStatusConfiguration;
import com.example.fbs.fbs.model.entity.Booking;
import com.example.fbs.fbs.model.entity.User;
import com.example.fbs.fbs.repository.UserRepository;
import com.example.fbs.fbs.service.impl.BookingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.example.fbs.fbs.utility.AuthUtils.extractTokenFromRequest;
import static com.example.fbs.fbs.utility.AuthUtils.hasClientAuthority;

@Tag(
        name = "Booking controller",
        description = """
                Controller to manipulate with bookings in the App.
                                
                This controller allows users to book one or multiple seats for a flight by specifying the flight ID
                and the number of seats. This operation is only available to logged-in and verified users 
                based on a JWT token generated during the user login. The JWT token should be copied 
                and set in the Authorization header as a Bearer token.
                                
                Users can also retrieve a list of all their bookings using the generated JWT token.
                                
                Additionally, users have the ability to cancel a booking by providing the booking ID.
                Upon cancellation, the seat count for the corresponding flight will be increased in the database."""
)
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/bookings/")
@SwaggerProfileApiResponseStatusConfiguration
public class BookingController {

    private final BookingServiceImpl bookingService;

    private final JwtService jwtService;

    private final HttpServletRequest request;

    private final UserRepository userRepository;


    @Operation(summary = "create new Booking in the app")
    @PostMapping()
    public ResponseEntity<Booking> bookFlight(@RequestParam("flightId") Long flightId, @RequestParam("seatCount") int seatCount) {
        String token = extractTokenFromRequest(request);

        if (!hasClientAuthority(jwtService, token)) {
            Booking booking = new Booking();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(booking);
        }
        String email = jwtService.extractEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingService.bookFlight(flightId, user, seatCount);
        return ResponseEntity.ok(booking);
    }

    @Operation(summary = "Get all bookings for the user")
    @GetMapping()
    public ResponseEntity<List<Booking>> getAllUserBookings() {
        String token = extractTokenFromRequest(request);

        if (!hasClientAuthority(jwtService, token)) {
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
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        String token = extractTokenFromRequest(request);

        if (!hasClientAuthority(jwtService, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking cancelled successfully.");
    }
}