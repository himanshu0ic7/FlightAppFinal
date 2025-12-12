package com.flightApp.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.flightApp.dto.FlightInventoryRequest;
import com.flightApp.dto.FlightResponse;
import com.flightApp.dto.FlightSearchRequest;

public interface FlightService {
	Mono<FlightResponse> addFlight(FlightInventoryRequest request);
    Flux<FlightResponse> searchFlights(FlightSearchRequest request);
    Flux<FlightResponse> getAllFlights();
    Mono<FlightResponse> getFlightById(String id);
}
