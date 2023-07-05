package com.example.fbs.fbs.repository;

import com.example.fbs.fbs.model.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findById(Long id);

    Page<Flight> findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
            String departureAirport, String arrivalAirport, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

}