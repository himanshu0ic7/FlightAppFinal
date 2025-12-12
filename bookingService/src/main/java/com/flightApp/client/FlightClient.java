package com.flightApp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.flightApp.dto.FlightDTO;

@FeignClient(name = "flightService")
public interface FlightClient {

    @GetMapping("/flight/{id}")
    FlightDTO getFlightById(@PathVariable("id") String id);
}
