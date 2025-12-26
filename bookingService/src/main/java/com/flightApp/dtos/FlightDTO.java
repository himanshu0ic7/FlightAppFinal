package com.flightApp.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FlightDTO {
    private String flightId;
    private String airlineName;
    private float price;
    private Integer availableSeats;
    private Integer totalSeats;
    private LocalDateTime flightDateTime;
    private LocalDateTime flightEndDateTime;
    private Airports fromPlace;
    private Airports toPlace;
}
