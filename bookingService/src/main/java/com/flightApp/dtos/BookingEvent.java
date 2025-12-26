package com.flightApp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingEvent {
    private String pnr;
    private String email;
    private String message;
    private String mobileNumber;
}
