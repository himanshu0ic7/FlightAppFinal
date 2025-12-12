package com.flightApp;


import com.flightApp.dto.FlightInventoryRequest;
import com.flightApp.dto.FlightResponse;
import com.flightApp.dto.FlightSearchRequest;
import com.flightApp.model.Airports;
import com.flightApp.model.Flight;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ModelCoverageTest {

    @Test
    void testFlightModel() {
        Flight flight = new Flight();
        flight.setFlightId("1");
        flight.setAirlineName("IndiGo");
        flight.setPrice(100.0f);
        flight.setTotalSeats(100);
        flight.setAvailableSeats(50);
        flight.setFromPlace(Airports.DEL);
        flight.setToPlace(Airports.BOM);
        flight.setFlightDateTime(LocalDateTime.now());
        flight.setVersion(1L);

        assertEquals("1", flight.getFlightId());
        assertEquals("IndiGo", flight.getAirlineName());
        assertEquals(100.0f, flight.getPrice());
        assertNotNull(flight.toString()); // Covers toString()
        assertNotEquals(new Flight(), flight); // Covers equals/hashCode
    }

    @Test
    void testFlightSearchRequest() {
        FlightSearchRequest req = new FlightSearchRequest();
        req.setFromPlace(Airports.DEL);
        req.setToPlace(Airports.BOM);
        req.setJourneyDate(LocalDate.now());

        assertEquals(Airports.DEL, req.getFromPlace());
        assertNotNull(req.toString());
    }

    @Test
    void testFlightInventoryRequest() {
        FlightInventoryRequest req = new FlightInventoryRequest();
        req.setAirlineName("Test");
        req.setPrice(500);
        // Set other fields...
        
        assertEquals("Test", req.getAirlineName());
    }

    @Test
    void testFlightResponse() {
        FlightResponse res = new FlightResponse();
        res.setFlightId("123");
        // Set other fields...
        
        assertEquals("123", res.getFlightId());
    }
}
