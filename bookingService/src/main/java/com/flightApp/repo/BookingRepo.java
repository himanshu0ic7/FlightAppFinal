package com.flightApp.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.flightApp.model.Booking;
import com.flightApp.model.BookingStatus;

public interface BookingRepo extends MongoRepository<Booking, String> {
    
    Optional<Booking> findByPnrNumber(String pnrNumber);
    List<Booking> findByUserId(String userId);
    List<Booking> findByFlightIdAndBookingStatus(String flightId, BookingStatus bookingStatus);
}
