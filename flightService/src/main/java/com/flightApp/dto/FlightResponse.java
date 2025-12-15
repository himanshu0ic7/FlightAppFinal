package com.flightApp.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.flightApp.model.Airports;

@Data
public class FlightResponse {
    private String flightId;
    private String airlineName;
    private Airports fromPlace;
    private Airports toPlace;
    private LocalDateTime flightDateTime;
    private LocalDateTime flightEndDateTime;
    private float price;
    private Integer availableSeats;
    private Integer totalSeats;
}
