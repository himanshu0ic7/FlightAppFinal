package com.flightApp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import com.flightApp.client.FlightClient;
import com.flightApp.dto.BookingRequest;
import com.flightApp.dto.BookingResponse;
import com.flightApp.dto.FlightDTO;
import com.flightApp.dto.PassengerDTO;
import com.flightApp.model.Booking;
import com.flightApp.model.BookingStatus;
import com.flightApp.model.Gender;
import com.flightApp.model.Passenger;
import com.flightApp.model.User;
import com.flightApp.repo.BookingRepo;
import com.flightApp.repo.UserRepo;
import com.flightApp.service.BookingServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepo bookingRepository;
    
    @Mock
    private UserRepo userRepository;

    @Mock
    private FlightClient flightClient;

    @Mock
    private KafkaTemplate kafkaTemplate; 

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bookingService, "kafkaTemplate", kafkaTemplate);
    }

    @Test
    void bookTicket_Success() {
        BookingRequest request = new BookingRequest();
        request.setFlightId("F100");
        request.setNumberOfSeats(1);
        request.setEmailId("test@demo.com");
        
        List<PassengerDTO> passengers = new ArrayList<>();
        PassengerDTO p1 = new PassengerDTO();
        p1.setName("John Doe");
        p1.setAge(25);
        p1.setGender(Gender.MALE);
        passengers.add(p1);
        request.setPassengers(passengers);
        
        FlightDTO mockFlight = new FlightDTO();
        mockFlight.setFlightId("F100");
        mockFlight.setAirlineName("Indigo");
        mockFlight.setPrice(5000.0f);
        mockFlight.setAvailableSeats(10);
        mockFlight.setTotalSeats(100); 
        
        when(flightClient.getFlightById("F100")).thenReturn(mockFlight);
        
        User mockUser = new User();
        mockUser.setEmailId("test@demo.com");
        mockUser.setMobileNumber("9876543210");
        mockUser.setName("John Doe");
        
        Booking savedBooking = new Booking();
        savedBooking.setPnrNumber("PNR123");
        savedBooking.setBookingStatus(BookingStatus.CONFIRMED);
        savedBooking.setPassengers(new ArrayList<>()); 

        lenient().when(userRepository.findByEmailId(anyString()))
                 .thenReturn(Optional.of(mockUser));
        
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        lenient().when(kafkaTemplate.send(any(), any())).thenReturn(future);

        BookingResponse response = bookingService.bookTicket(request);

        assertNotNull(response);
        assertEquals("PNR123", response.getPnrNumber());
    }

    @Test
    void getTicket_Success() {
        Booking booking = new Booking();
        booking.setPnrNumber("PNR123");
        booking.setFlightId("F100");
        
        List<Passenger> passengerList = new ArrayList<>();
        Passenger p = new Passenger();
        p.setName("Test Passenger");
        passengerList.add(p);
        booking.setPassengers(passengerList);
        
        FlightDTO mockFlight = new FlightDTO();
        mockFlight.setFlightId("F100");
        mockFlight.setAirlineName("Indigo");
        mockFlight.setPrice(5000.0f);
        mockFlight.setAvailableSeats(10);
        mockFlight.setTotalSeats(100);
        
        when(flightClient.getFlightById("F100")).thenReturn(mockFlight);
        
        lenient().when(bookingRepository.findById("PNR123")).thenReturn(Optional.of(booking));
        
        try {
             lenient().when(bookingRepository.findByPnrNumber("PNR123")).thenReturn(Optional.of(booking));
        } catch (Exception e) { }

        var response = bookingService.getTicketDetails("PNR123");
        
        assertEquals("PNR123", response.getPnrNumber());
    }
}