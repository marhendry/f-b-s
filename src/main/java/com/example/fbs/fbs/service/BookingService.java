package com.example.fbs.fbs.service;

import com.example.fbs.fbs.model.entity.Booking;
import com.example.fbs.fbs.model.entity.Flight;
import com.example.fbs.fbs.model.entity.User;

import java.util.List;

public interface BookingService {

    public Booking bookFlight(Long flightId, User user, int seatCount);

//    public List<Booking> getUserBookings(User user);
//
//    public void cancelBooking(Booking booking);
}