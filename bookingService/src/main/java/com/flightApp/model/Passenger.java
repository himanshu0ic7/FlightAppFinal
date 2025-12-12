package com.flightApp.model;

import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Passenger {
    @NotBlank(message = "Passenger name is required")
    private String name;
    
    @NotNull(message = "Gender is required")
    private Gender gender;
    
    private int age;
    private Integer seatNumber;
    
    @NotNull
    @Indexed
    private String passengerId;
    
    
}
