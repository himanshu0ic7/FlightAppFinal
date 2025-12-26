package com.flightApp.dtos;

import lombok.Data;
import java.time.LocalDateTime;

import com.flightApp.model.BookingStatus;

@Data
public class BookingHistoryResponse {
    private String pnrNumber;
    private LocalDateTime journeyDateTime;
    private LocalDateTime journeyEndDateTime;
    private Airports fromPlace;
    private Airports toPlace;
    private BookingStatus status; 
}
