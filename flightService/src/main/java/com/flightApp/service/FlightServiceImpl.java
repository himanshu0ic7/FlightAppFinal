package com.flightApp.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightApp.dto.FlightInventoryRequest;
import com.flightApp.dto.FlightResponse;
import com.flightApp.dto.FlightSearchRequest;
import com.flightApp.model.Flight;
import com.flightApp.repo.FlightRepo;
import com.flightApp.validation.InvalidAirportCodeException;
import com.flightApp.validation.ResourceNotFoundException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class FlightServiceImpl implements FlightService {

	@Autowired
    private FlightRepo flightRepository;

	@Override
    public Mono<FlightResponse> addFlight(FlightInventoryRequest request) {
        if (request.getFromPlace().equals(request.getToPlace())) {
            return Mono.error(new InvalidAirportCodeException("Source and Destination airports cannot be the same"));
        }

        Flight flight = new Flight();
        flight.setAirlineName(request.getAirlineName());
        flight.setFromPlace(request.getFromPlace());
        flight.setToPlace(request.getToPlace());
        flight.setFlightDateTime(request.getFlightDateTime());
        
        flight.setPrice(request.getPrice());
        flight.setTotalSeats(request.getTotalSeats());
        flight.setAvailableSeats(request.getTotalSeats());

        return flightRepository.save(flight).map(this::mapToDTO);
    }

    @Override
    public Flux<FlightResponse> searchFlights(FlightSearchRequest request) {
        if(request.getFromPlace().equals(request.getToPlace())) {
             return Flux.error(new InvalidAirportCodeException("From and To airports cannot be the same"));
        }

        LocalDateTime startOfDay = request.getJourneyDate().atStartOfDay();
        LocalDateTime endOfDay = request.getJourneyDate().atTime(java.time.LocalTime.MAX);

        return flightRepository.findByFromPlaceAndToPlaceAndFlightDateTimeBetween(
                request.getFromPlace(), 
                request.getToPlace(), 
                startOfDay, 
                endOfDay
        ).map(this::mapToDTO)
         .switchIfEmpty(Flux.error(new ResourceNotFoundException("No flights found for the given criteria")));
    }

    @Override
    public Flux<FlightResponse> getAllFlights() {
        return flightRepository.findAll().map(this::mapToDTO);
    }

    @Override
    public Mono<FlightResponse> getFlightById(String id) {
        return flightRepository.findById(id)
                .map(this::mapToDTO)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Flight not found with id: " + id)));
    }

    private FlightResponse mapToDTO(Flight flight) {
        FlightResponse response = new FlightResponse();
        BeanUtils.copyProperties(flight, response);
        response.setFlightId(flight.getFlightId());
        response.setTotalSeats(flight.getTotalSeats());
        response.setFlightDateTime(flight.getFlightDateTime());
        return response;
    }
}
