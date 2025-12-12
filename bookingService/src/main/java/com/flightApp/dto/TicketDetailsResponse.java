package com.flightApp.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketDetailsResponse {
    private String pnrNumber;
    private LocalDateTime journeyDateTime;
    private FlightDetails flight;
    private List<PassengerDTO> passengers;
    private com.flightApp.model.BookingStatus status; 
    
    @Data
    public static class FlightDetails {
        private String airlineName;
        private Airports fromPlace;
        private Airports toPlace;
        private String flightId;
    }
}