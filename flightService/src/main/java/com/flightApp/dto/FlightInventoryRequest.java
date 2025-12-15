package com.flightApp.dto;

import java.time.LocalDateTime;

import com.flightApp.model.Airports;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FlightInventoryRequest {
	@NotBlank(message = "Airline name is required")
    @Size(min = 2, max = 50, message = "Airline name must be between 2 and 50 characters")
    private String airlineName;
    
	@NotNull(message = "From place is required")
    private Airports fromPlace;

    @NotNull(message = "To place is required")
    private Airports toPlace;

    @Future(message = "Flight date/time must be in the future")
    @NotNull(message = "Flight date/time is required")
    private LocalDateTime flightDateTime;

    @Future(message = "Flight date/time must be in the future")
    @NotNull(message = "Flight date/time is required")
    private LocalDateTime flightEndDateTime;
    
    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private float price;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "There must be at least 1 seat")
    private Integer totalSeats;
}
