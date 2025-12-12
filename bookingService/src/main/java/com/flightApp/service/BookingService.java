package com.flightApp.service;

import java.util.List;


import com.flightApp.dto.BookingHistoryResponse;
import com.flightApp.dto.BookingRequest;
import com.flightApp.dto.BookingResponse;
import com.flightApp.dto.TicketDetailsResponse;

public interface BookingService {

	BookingResponse bookTicket(BookingRequest request);

	void cancelBooking(String pnr);

	TicketDetailsResponse getTicketDetails(String pnr);

	List<BookingHistoryResponse> getBookingHistory(String email);

}