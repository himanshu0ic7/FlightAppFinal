package com.flightApp.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.flightApp.model.Airports;
import com.flightApp.model.Flight;

import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Repository
public interface FlightRepo extends ReactiveMongoRepository<Flight, String> {
	Flux<Flight> findByFromPlaceAndToPlaceAndFlightDateTimeBetween(
            Airports from, 
            Airports to, 
            LocalDateTime start, 
            LocalDateTime end
    );
}
