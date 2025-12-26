package com.flightApp.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private String message;
    private String pnrNumber;
    
    private List<PassengerDetail> passengers; 
    
    @Data
    @AllArgsConstructor
    public static class PassengerDetail {
        private String name;
        private int seatNumber;
    }
}
