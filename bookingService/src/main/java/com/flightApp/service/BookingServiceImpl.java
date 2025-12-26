package com.flightApp.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.flightApp.client.AuthClient;
import com.flightApp.client.FlightClient;
import com.flightApp.dtos.BookingEvent;
import com.flightApp.dtos.BookingHistoryResponse;
import com.flightApp.dtos.BookingRequest;
import com.flightApp.dtos.BookingResponse;
import com.flightApp.dtos.FlightDTO;
import com.flightApp.dtos.PassengerDTO;
import com.flightApp.dtos.TicketDetailsResponse;
import com.flightApp.dtos.UserDto;
import com.flightApp.model.Booking;
import com.flightApp.model.BookingStatus;
import com.flightApp.model.Passenger;
import com.flightApp.repo.BookingRepo;
import com.flightApp.validation.BookingException;
import com.flightApp.validation.ResourceNotFoundException;

import feign.FeignException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Validated
@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepo bookingRepository;

    @Autowired
    private FlightClient flightClient;

    @Autowired
    private AuthClient authClient;
    
    @Autowired
    private KafkaTemplate<String, BookingEvent> kafkaTemplate;
    
    @Transactional
    public BookingResponse bookTicket(BookingRequest request) {
        
        if (request.getNumberOfSeats() != request.getPassengers().size()) {
            throw new BookingException("Mismatch: Number of seats requested does not match passenger list size.");
        }
        
        String userId=null;
        UserDto secUser=null;
        try {
            secUser = authClient.getUserByUserName(request.getName());
            userId = secUser.getId();
        } catch (FeignException.NotFound e) {
            throw new BookingException("You must be a registered user to book a ticket. Please log in.");
        } catch(Exception e) {
        	System.out.println(e.getMessage());
        }

        FlightDTO flight;
        flight = flightClient.getFlightById(request.getFlightId());

        if (flight.getAvailableSeats() < request.getNumberOfSeats()) {
            throw new BookingException("Not enough seats available.");
        }

        List<Booking> existingBookings = bookingRepository.findByFlightIdAndBookingStatus(
                request.getFlightId(), BookingStatus.CONFIRMED);
        
        List<Integer> assignedSeats = allocateSeats(existingBookings, flight.getTotalSeats(), request.getNumberOfSeats());

        Booking booking = new Booking();
        booking.setPnrNumber("PNR" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        booking.setFlightId(request.getFlightId());
        booking.setUserId(userId); 
        
        booking.setNoOfSeats(request.getNumberOfSeats());
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setJourneyDateTime(flight.getFlightDateTime());
        booking.setJourneyEndDateTime(flight.getFlightEndDateTime());
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setTotalPrice(flight.getPrice() * request.getNumberOfSeats());

        List<Passenger> entityPassengers = new ArrayList<>();
        List<BookingResponse.PassengerDetail> responsePassengers = new ArrayList<>();

        for (int i = 0; i < request.getPassengers().size(); i++) {
            PassengerDTO dto = request.getPassengers().get(i);
            int seat = assignedSeats.get(i);

            Passenger p = new Passenger();
            p.setName(dto.getName());
            p.setGender(dto.getGender());
            p.setAge(dto.getAge());
            p.setSeatNumber(seat);
            p.setPassengerId(dto.getPassengerId());
            entityPassengers.add(p);
            
            responsePassengers.add(new BookingResponse.PassengerDetail(dto.getName(), seat));
        }
        booking.setPassengers(entityPassengers);

        Booking savedBooking = bookingRepository.save(booking);
        try {
            flightClient.updateAvailableSeats(request.getFlightId(), request.getNumberOfSeats());
        } catch (Exception e) {
            System.err.println("Failed to update flight inventory: " + e.getMessage());
        }
        sendKafkaNotification(
                savedBooking.getPnrNumber(), 
                secUser.getMobileNumber(),
                secUser.getEmail(), 
                "Booking Confirmed! PNR: " + savedBooking.getPnrNumber()
            );

        return new BookingResponse("Booking successful", savedBooking.getPnrNumber(), responsePassengers);
    }

    @Transactional
    public void cancelBooking(String pnr) {
        Booking booking = bookingRepository.findByPnrNumber(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled.");
        }
        
        if (booking.getJourneyDateTime().minusHours(24).isBefore(LocalDateTime.now())) {
            throw new BookingException("Cannot cancel ticket within 24 hours of journey time.");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        try {
            flightClient.updateAvailableSeats(booking.getFlightId(), -booking.getNoOfSeats());
            
            System.out.println("Restocked " + booking.getNoOfSeats() + " seats for flight " + booking.getFlightId());
        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to restock seats. Error: " + e.getMessage());
        }
        UserDto user = authClient.getUserById(booking.getUserId());

        sendKafkaNotification(
                booking.getPnrNumber(), 
                user != null ? user.getMobileNumber() : "", 
                user != null ? user.getEmail() : "", 
                "Booking Cancelled Successfully for PNR: " + booking.getPnrNumber()
            );
    }

    public TicketDetailsResponse getTicketDetails(String pnr) {
        Booking booking = bookingRepository.findByPnrNumber(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not Found for PNR: "+ pnr));

        FlightDTO flight = flightClient.getFlightById(booking.getFlightId());

        TicketDetailsResponse response = new TicketDetailsResponse();
        response.setPnrNumber(booking.getPnrNumber());
        response.setJourneyDateTime(booking.getJourneyDateTime());
        response.setJourneyEndDateTime(booking.getJourneyEndDateTime());
        response.setStatus(booking.getBookingStatus());

        TicketDetailsResponse.FlightDetails fd = new TicketDetailsResponse.FlightDetails();
        fd.setAirlineName(flight.getAirlineName());
        fd.setFlightId(booking.getFlightId());
        fd.setFromPlace(flight.getFromPlace());
        fd.setToPlace(flight.getToPlace());
        response.setFlight(fd);

        List<PassengerDTO> passengerDTOS = booking.getPassengers().stream().map(p -> {
            PassengerDTO dto = new PassengerDTO();
            dto.setName(p.getName());
            dto.setGender(p.getGender());
            dto.setAge(p.getAge());
            dto.setSeatNumber(p.getSeatNumber());
            return dto;
        }).collect(Collectors.toList());
        response.setPassengers(passengerDTOS);

        return response;
    }
    
    public List<BookingHistoryResponse> getBookingHistory(String email) {
    	UserDto user = null;
    	try {
    		user = authClient.getUserByEmail(email);
    		System.out.println("getUserByEmail Successfull");
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    	

    	if (user == null) {
    	    throw new ResourceNotFoundException("User not found with email: " + email);
    	}


        List<Booking> bookings = bookingRepository.findByUserId(user.getId());

        if (bookings.isEmpty()) {
            throw new ResourceNotFoundException("No bookings found for email: " + email);
        }

        return bookings.stream().map(b -> {
            BookingHistoryResponse response = new BookingHistoryResponse();
            response.setPnrNumber(b.getPnrNumber());
            response.setJourneyDateTime(b.getJourneyDateTime());
            response.setJourneyEndDateTime(b.getJourneyEndDateTime());
            response.setStatus(b.getBookingStatus());

            try {
                FlightDTO flight = flightClient.getFlightById(b.getFlightId());
                response.setFromPlace(flight.getFromPlace());
                response.setToPlace(flight.getToPlace());
            } catch (Exception e) {
                // Ignore flight service errors for history
            }
            return response;
        }).collect(Collectors.toList());
    }


    private void sendKafkaNotification(String pnr, String mobileNumber, String email, String msg) {
        try {
        	BookingEvent event = BookingEvent.builder()
                    .pnr(pnr)
                    .mobileNumber(mobileNumber)
                    .email(email)
                    .message(msg)
                    .build();
            
            kafkaTemplate.send("flight_confirmation", event);
        }catch(Exception e) {
        	System.err.println("WARNING: Kafka is down or unreachable. Notification failed for PNR: " + pnr);
            System.err.println("Error: " + e.getMessage());
        }
    	
    }

   
    private List<Integer> allocateSeats(List<Booking> existingBookings, int totalSeats, int seatsNeeded) {
        Set<Integer> occupiedSeats = new HashSet<>();

        for (Booking b : existingBookings) {
            if (b.getPassengers() != null) {
                b.getPassengers().forEach(p -> {
                    if (p.getSeatNumber() != null) {
                        occupiedSeats.add(p.getSeatNumber());
                    }
                });
            }
        }

        List<Integer> newSeats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++) {
            if (!occupiedSeats.contains(i)) {
                newSeats.add(i);
                if (newSeats.size() == seatsNeeded) {
                    break;
                }
            }
        }

        if (newSeats.size() < seatsNeeded) {
            throw new BookingException("Data Inconsistency: System indicates seats available, but seat allocation failed (Fragmentation/Error).");
        }

        return newSeats;
    }
}