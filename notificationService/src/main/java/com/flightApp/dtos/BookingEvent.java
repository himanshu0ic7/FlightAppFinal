package com.flightApp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingEvent {
    private String pnr;
    private String email;
    private String mobileNumber;
    private String message;
}
