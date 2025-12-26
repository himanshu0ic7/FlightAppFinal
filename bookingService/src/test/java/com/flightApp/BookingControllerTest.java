package com.flightApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightApp.controller.BookingController;
import com.flightApp.dtos.BookingResponse;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(BookingController.class)
public class BookingControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private BookingService bookingService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Test
//    void bookTicket_Success() throws Exception {
//        BookingResponse response = new BookingResponse();
//        response.setPnrNumber("PNR123");
//
//        Mockito.when(bookingService.bookTicket(any())).thenReturn(response);
//
//        String json = """
//            {
//                "flightId": "F100",
//                "emailId": "test@demo.com",
//                "numberOfSeats": 2,
//                "name": "John Doe",
//                "mobileNumber": "9999999999"
//            }
//        """;
//
//        mockMvc.perform(post("/booking")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json))
//                // CORRECT SYNTAX: .andExpect(...)
//                .andExpect(status().isCreated()) 
//                .andExpect(jsonPath("$.pnr").value("PNR123"));
//    }
//
//    @Test
//    void bookTicket_ValidationFail() throws Exception {
//        // Empty body triggers @Valid failure
//        mockMvc.perform(post("/booking")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{}"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Validation Failed"));
//    }
//
//    @Test
//    void getTicket_NotFound() throws Exception {
//        Mockito.when(bookingService.getTicketDetails("INVALID_PNR"))
//               .thenThrow(new ResourceNotFoundException("PNR not found"));
//
//        mockMvc.perform(get("/booking/ticket/INVALID_PNR"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.error").value("Resource Not Found"));
//    }
//
//    @Test
//    void bookTicket_FlightServiceDown() throws Exception {
//        FeignException feignEx = Mockito.mock(FeignException.class);
//        Mockito.when(feignEx.status()).thenReturn(503);
//        
//        Mockito.when(bookingService.bookTicket(any()))
//               .thenThrow(feignEx);
//               
//        String json = """
//            {
//                "flightId": "F100",
//                "emailId": "test@demo.com",
//                "numberOfSeats": 2,
//                "name": "John Doe",
//                "mobileNumber": "9999999999"
//            }
//        """;
//
//        mockMvc.perform(post("/booking")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json))
//                .andExpect(status().isServiceUnavailable())
//                .andExpect(jsonPath("$.error").value("Dependency Error"));
//    }
}