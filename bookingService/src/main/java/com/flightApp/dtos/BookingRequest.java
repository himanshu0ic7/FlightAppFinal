package com.flightApp.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;


@Data
public class BookingRequest {
    @NotBlank(message = "User name is required")
    private String name;
    
    @NotBlank(message = "Flight ID is required")
    private String flightId;

    @Min(value = 1, message = "Must book at least 1 seat")
    private int numberOfSeats;

    @Valid 
    @NotEmpty(message = "Passenger list cannot be empty")
    @Size(min = 1, message = "Must have at least 1 passenger")
    private List<PassengerDTO> passengers;
}