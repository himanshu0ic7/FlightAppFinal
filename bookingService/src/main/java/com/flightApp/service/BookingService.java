package com.flightApp.service;

import java.util.List;

import com.flightApp.dtos.BookingHistoryResponse;
import com.flightApp.dtos.BookingRequest;
import com.flightApp.dtos.BookingResponse;
import com.flightApp.dtos.TicketDetailsResponse;

public interface BookingService {

	BookingResponse bookTicket(BookingRequest request);

	void cancelBooking(String pnr);

	TicketDetailsResponse getTicketDetails(String pnr);

	List<BookingHistoryResponse> getBookingHistory(String email);

}