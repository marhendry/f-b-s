package com.example.fbs.fbs.repository;

import com.example.fbs.fbs.model.entity.Booking;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
//    List<Booking> findByUser (User user);
//
//    Optional<Booking> findBookingBy(Booking id);
}