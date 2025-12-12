package com.flightApp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;


@Data
public class BookingRequest {
    @NotBlank(message = "User name is required")
    private String name;

    @NotBlank(message = "Email ID is required")
    @Email(message = "A valid email address is required")
    private String emailId;
    
    @NotBlank(message = "Flight ID is required")
    private String flightId;

    @Min(value = 1, message = "Must book at least 1 seat")
    private int numberOfSeats;

    @Valid 
    @NotEmpty(message = "Passenger list cannot be empty")
    @Size(min = 1, message = "Must have at least 1 passenger")
    private List<PassengerDTO> passengers;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid mobile number format. Example: +919999999999")
    private String mobileNumber;
}