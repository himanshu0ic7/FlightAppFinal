package com.flightApp.model;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import jakarta.validation.constraints.Positive;

@Data
@Document(collection="flights")
@CompoundIndexes({
    @CompoundIndex(name = "unique_flight_index",
                   def = "{'airlineName': 1, 'flightDateTime': 1}",
                   unique = true)
})
public class Flight {
    
    @Id
    String flightId;
    
    String airlineName;
    
    @NotNull(message="from place is required")
    Airports fromPlace;
    
    @NotNull(message="to place is required")
    Airports toPlace;
    
    @Version
    private Long version;
    
    LocalDateTime flightDateTime;
    
    @Positive(message="Price cannot be negative")
    float price;
    
    @Positive(message = "Available seats must be greater than zero")
    int totalSeats;
    
    int availableSeats;
}
