package com.flightApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.flightApp.dto.FlightInventoryRequest;
import com.flightApp.dto.FlightResponse;
import com.flightApp.dto.FlightSearchRequest;
import com.flightApp.service.FlightService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/flight")
public class FlightController {

	@Autowired
    private FlightService flightService;

	@PostMapping("/airline/inventory")
    public Mono<FlightResponse> addInventory(@Valid @RequestBody FlightInventoryRequest request) {
        return flightService.addFlight(request);
    }
    
    @PostMapping("/search")
    public Flux<FlightResponse> searchFlights(@Valid @RequestBody FlightSearchRequest request) {
        return flightService.searchFlights(request);
    }

    @GetMapping("/all")
    public Flux<FlightResponse> getAllFlights() {
        return flightService.getAllFlights();
    }

    @GetMapping("/{id}")
    public Mono<FlightResponse> getFlightById(@PathVariable String id) {
        return flightService.getFlightById(id);
    }
    
    @PutMapping("/updateSeats/{flightId}")
    public Mono<ResponseEntity<String>> updateSeats(
            @PathVariable String flightId, 
            @RequestParam int count
    ) {
        return flightService.updateSeatInventory(flightId, count)
            .map(updatedFlight -> ResponseEntity.ok("Seats updated successfully. Remaining: " + updatedFlight.getAvailableSeats()))
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }
}
