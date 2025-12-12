package com.flightApp.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

import com.flightApp.model.Airports;

@Data
public class FlightSearchRequest {

	@NotNull(message = "Origin airport (From) is required")
    private Airports fromPlace;

	@NotNull(message = "Origin airport (From) is required")
    private Airports toPlace;

    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Flight date cannot be in the past")
    private LocalDate journeyDate;
}
