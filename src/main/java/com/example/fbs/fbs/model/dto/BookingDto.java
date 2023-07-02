package com.example.fbs.fbs.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private Long id;

    private String userId;

    private Long flightId;

    private LocalDateTime bookingTime;

    private int seatNumber;
}