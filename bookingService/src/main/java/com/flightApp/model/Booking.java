package com.flightApp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "bookings")
@CompoundIndexes({
    @CompoundIndex(name = "unique_seat_per_flight", 
                   def = "{'flightId': 1, 'passengers.seatNumber': 1}", 
                   unique = true, 
                   partialFilter = "{'status': 'CONFIRMED'}"),
    @CompoundIndex(name = "unique_passenger_per_flight", 
    def = "{'flightId': 1, 'passengers.passengerId': 1}", 
    unique = true, 
    partialFilter = "{'bookingStatus': 'CONFIRMED'}")
})
public class Booking {
    
    @Id
    String id;
    
    String pnrNumber;           
    LocalDateTime bookingDateTime;
    LocalDateTime journeyDateTime;
    
    @Version
    private Long version;
    
    @NotNull
    int noOfSeats;
    
    BookingStatus bookingStatus;
    
    private String userId;
    private String flightId;
    
    List<Passenger> passengers;
    
    private float totalPrice;
}
