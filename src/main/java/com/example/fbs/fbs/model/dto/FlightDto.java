package com.example.fbs.fbs.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(example = "1")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "Airport A")
    @JsonProperty("departureAirport")
    private String departureAirport;

    @Schema(example = "Airport B")
    @JsonProperty("arrivalAirport")
    private String arrivalAirport;

    @Schema(example = "2023-07-01 10:00")
    @JsonProperty("departureTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime departureTime;

    @Schema(example = "2023-07-01 12:00")
    @JsonProperty("arrivalTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime arrivalTime;

    @Schema(example = "140")
    @JsonProperty("seats")
    private int seats;
}
