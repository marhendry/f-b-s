package com.example.fbs.fbs.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookingDto {

    private Long id;

    private String userId;

    private Long flightId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime bookingTime;

    private int seatNumber;
}