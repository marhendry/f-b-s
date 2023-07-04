package com.example.fbs.fbs.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class FlightDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("departureAirport")
    private String departureAirport;

    @JsonProperty("arrivalAirport")
    private String arrivalAirport;

    @JsonProperty("departureTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime departureTime;

    @JsonProperty("arrivalTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime arrivalTime;

    @JsonProperty("seats")
    private int seats;
}
