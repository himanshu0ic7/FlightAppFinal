package com.flightApp;

import com.flightApp.dto.BookingRequest;
import com.flightApp.dto.BookingResponse;
import com.flightApp.dto.FlightDTO;
import com.flightApp.dto.PassengerDTO;
import com.flightApp.model.BookingStatus;
import com.flightApp.model.Gender;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PojoTest {

    @Test
    void testBookingRequest() {
        BookingRequest req = new BookingRequest();
        req.setFlightId("F100");
        req.setNumberOfSeats(2);
        req.setEmailId("test@test.com");
        req.setName("John");
        req.setMobileNumber("1234567890");
        
        List<PassengerDTO> passengers = new ArrayList<>();
        req.setPassengers(passengers);

        assertEquals("F100", req.getFlightId());
        assertEquals(2, req.getNumberOfSeats());
        assertEquals("test@test.com", req.getEmailId());
        assertEquals("John", req.getName());
        assertEquals("1234567890", req.getMobileNumber());
        assertEquals(passengers, req.getPassengers());
        
        assertNotNull(req.toString()); // Covers toString()
        BookingRequest req2 = new BookingRequest();
        req2.setFlightId("F100");
        assertNotEquals(req, req2); // Covers equals/hashCode logic if present
    }

    @Test
    void testBookingResponse() {
        BookingResponse res = new BookingResponse();
        res.setPnrNumber("PNR123");
        assertEquals("PNR123", res.getPnrNumber());
        assertNotNull(res.toString());
    }

    @Test
    void testPassengerDTO() {
        PassengerDTO p = new PassengerDTO();
        p.setName("Test");
        p.setAge(25);
        p.setGender(Gender.MALE);

        assertEquals("Test", p.getName());
        assertEquals(25, p.getAge());
        assertEquals(Gender.MALE, p.getGender());
        assertNotNull(p.toString());
    }

    @Test
    void testFlightDTO() {
        FlightDTO f = new FlightDTO();
        f.setFlightId("F100");
        f.setPrice(100.0f);
        f.setAvailableSeats(10);
        f.setTotalSeats(100);
        f.setAirlineName("Indigo");

        assertEquals("F100", f.getFlightId());
        assertEquals(100.0, f.getPrice());
        assertEquals(10, f.getAvailableSeats());
        assertEquals(100, f.getTotalSeats());
        assertEquals("Indigo", f.getAirlineName());
    }
}
