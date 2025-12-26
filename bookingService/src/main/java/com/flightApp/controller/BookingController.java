package com.flightApp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.flightApp.dtos.ApiResponse;
import com.flightApp.dtos.BookingHistoryResponse;
import com.flightApp.dtos.BookingRequest;
import com.flightApp.dtos.BookingResponse;
import com.flightApp.dtos.TicketDetailsResponse;
import com.flightApp.service.BookingService;

import java.util.List;


@RestController
@RequestMapping("/booking")
public class BookingController {

	@Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> bookTicket(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.bookTicket(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/ticket/{pnr}")
    public ResponseEntity<TicketDetailsResponse> getTicket(@PathVariable String pnr) {
        return ResponseEntity.ok(bookingService.getTicketDetails(pnr));
    }

    @GetMapping("/history/{email}")
    public ResponseEntity<List<BookingHistoryResponse>> getHistory(@PathVariable String email) {
        return ResponseEntity.ok(bookingService.getBookingHistory(email));
    }

    @DeleteMapping("/cancel/{pnr}")
    public ResponseEntity<ApiResponse> cancelTicket(@PathVariable String pnr) {
        bookingService.cancelBooking(pnr);
        return ResponseEntity.ok(new ApiResponse("Ticket Cancelled Successfully for PNR: "+pnr));
    }
    
    @GetMapping("/seats/{flightId}")
    public ResponseEntity<List<Integer>> getOccupiedSeats(@PathVariable String flightId) {
        return ResponseEntity.ok(bookingService.getOccupiedSeats(flightId));
    }
}
