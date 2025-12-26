package com.flightApp.dtos;

import com.flightApp.model.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PassengerDTO {
    @NotBlank(message = "Passenger name is required")
    private String name;
    
    @NotNull(message = "Gender is required")
    private Gender gender;
    
    private int age;
    private Integer seatNumber;
    
    @NotBlank(message = "Passenger ID (Passport/Aadhar/Email) is required")
    private String passengerId;
}
