package com.flightApp;

import com.flightApp.dto.FlightInventoryRequest;
import com.flightApp.dto.FlightResponse;
import com.flightApp.dto.FlightSearchRequest;
import com.flightApp.model.Airports;
import com.flightApp.model.Flight;
import com.flightApp.repo.FlightRepo;
import com.flightApp.service.FlightServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTest {

    @Mock
    private FlightRepo flightRepository;

    @InjectMocks
    private FlightServiceImpl flightService;

    @Test
    void addFlight_Success() {
        FlightInventoryRequest request = new FlightInventoryRequest();
        request.setAirlineName("Indigo");
        request.setFromPlace(Airports.DEL);
        request.setToPlace(Airports.BOM);
        request.setPrice(5000);
        request.setTotalSeats(100);
        request.setFlightDateTime(LocalDateTime.now().plusDays(1));

        Flight savedFlight = new Flight();
        savedFlight.setFlightId("123");
        savedFlight.setAirlineName("Indigo");

        when(flightRepository.save(any(Flight.class))).thenReturn(Mono.just(savedFlight));

        Mono<FlightResponse> result = flightService.addFlight(request);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getAirlineName().equals("Indigo"))
                .verifyComplete();
    }

    @Test
    void getAllFlights_Success() {
        Flight flight1 = new Flight();
        flight1.setFlightId("1");
        
        when(flightRepository.findAll()).thenReturn(Flux.just(flight1));

        Flux<FlightResponse> result = flightService.getAllFlights();

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }
    
    @Test
    void getFlightById_Success() {
        Flight flight = new Flight();
        flight.setFlightId("101");
        
        when(flightRepository.findById("101")).thenReturn(Mono.just(flight));

        Mono<FlightResponse> result = flightService.getFlightById("101");

        StepVerifier.create(result)
                .expectNextMatches(r -> r.getFlightId().equals("101"))
                .verifyComplete();
    }
    
    @Test
    void searchFlights_Success() {
        Flight flight = new Flight();
        flight.setFlightId("555");
        flight.setAirlineName("Indigo");
        flight.setFromPlace(Airports.DEL);
        flight.setToPlace(Airports.BOM);
        flight.setPrice(5000);
        
        FlightSearchRequest request = new FlightSearchRequest();
        request.setFromPlace(Airports.DEL);
        request.setToPlace(Airports.BOM);
        request.setJourneyDate(LocalDate.now());

        when(flightRepository.findByFromPlaceAndToPlaceAndFlightDateTimeBetween(
                eq(Airports.DEL),
                eq(Airports.BOM),
                any(LocalDateTime.class),
                any(LocalDateTime.class) 
        )).thenReturn(Flux.just(flight));

        Flux<FlightResponse> result = flightService.searchFlights(request);

        StepVerifier.create(result)
                .expectNextMatches(f -> f.getFlightId().equals("555"))
                .verifyComplete();
    }
}
