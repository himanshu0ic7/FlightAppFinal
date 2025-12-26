package com.flightApp;

import com.flightApp.controller.FlightController;
import com.flightApp.dto.FlightResponse;
import com.flightApp.dto.FlightSearchRequest;
import com.flightApp.model.Airports;
import com.flightApp.service.FlightService;
import com.flightApp.validation.AirportException;
import com.flightApp.validation.InvalidAirportCodeException;
import com.flightApp.validation.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(FlightController.class)
public class FlightControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FlightService flightService;

    @Test
    void searchFlights_ShouldReturnFlights() {
        FlightResponse mockFlight = new FlightResponse();
        mockFlight.setFlightId("123");

        Mockito.when(flightService.searchFlights(any()))
                .thenReturn(Flux.just(mockFlight));

        FlightSearchRequest request = new FlightSearchRequest();
        request.setFromPlace(Airports.DEL);
        request.setToPlace(Airports.BOM);
        request.setJourneyDate(LocalDate.now().plusDays(1));

        webTestClient.post()
                .uri("/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FlightResponse.class)
                .hasSize(1);
    }

    @Test
    void getAllFlights_Success() {
        Mockito.when(flightService.getAllFlights()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/flight/all")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void searchFlights_InvalidEnum_ShouldReturn400() {
        String badJson = "{ \"fromPlace\": \"Mars\" }";

        webTestClient.post()
                .uri("/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid Input")
                .jsonPath("$.status").isEqualTo(400);
    }

//    @Test
//    void addInventory_Success() {
//        FlightResponse response = new FlightResponse();
//        response.setFlightId("999");
//
//        Mockito.when(flightService.addFlight(any()))
//                .thenReturn(Mono.just(response));
//
//        String json = """
//            {
//                "airlineName": "Vistara",
//                "fromPlace": "DEL",
//                "toPlace": "BOM",
//                "flightDateTime": "2025-12-25T10:00:00",
//                "price": 5000,
//                "totalSeats": 100
//            }
//        """;
//
//        webTestClient.post()
//                .uri("/flight/airline/inventory")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(validFlightRequest())
//                .exchange()
//                .expectStatus().isBadRequest()
//                .expectBody()
//                .jsonPath("$.flightId").exists();
//    }

    @Test
    void getFlightById_Success() {
        FlightResponse response = new FlightResponse();
        response.setFlightId("101");

        Mockito.when(flightService.getFlightById("101"))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/flight/101")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.flightId").isEqualTo("101");
    }

    @Test
    void addInventory_ValidationFailure_ShouldReturn400() {
        webTestClient.post()
                .uri("/flight/airline/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Validation Failed")
                .jsonPath("$.details").exists()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void getFlightById_NotFound_ShouldReturn404() {
        Mockito.when(flightService.getFlightById("unknown"))
                .thenThrow(new ResourceNotFoundException("Flight not found"));

        webTestClient.get()
                .uri("/flight/unknown")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Resource Not Found")
                .jsonPath("$.message").isEqualTo("Flight not found");
    }

    @Test
    void getAllFlights_InternalError_ShouldReturn500() {
        Mockito.when(flightService.getAllFlights())
                .thenThrow(new RuntimeException("DB down"));

        webTestClient.get()
                .uri("/flight/all")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Internal Server Error");
    }

//    @Test
//    void searchFlight_InvalidAirport_ShouldReturn400() {
//        Mockito.when(flightService.searchFlights(any()))
//                .thenThrow(new InvalidAirportCodeException("Invalid Airport"));
//
//        String json = """
//            {
//                "fromPlace": "DEL",
//                "toPlace": "BOM",
//                "journeyDate": "2025-12-25"
//            }
//        """;
//
//        webTestClient.post()
//                .uri("/flight/search")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(json)
//                .exchange()
//                .expectStatus().isBadRequest()
//                .expectBody()
//                .jsonPath("$.message").isEqualTo("Invalid Airport");
//    }

//    @Test
//    void addInventory_DuplicateFlight_ShouldReturn409() {
//    	Mockito.when(flightService.addFlight(any()))
//        .thenThrow(new DuplicateKeyException("Duplicate"));
//
//
//    	webTestClient.post()
//        .uri("/flight/airline/inventory")
//        .contentType(MediaType.APPLICATION_JSON)
//        .bodyValue(validFlightRequest())
//        .exchange()
//        .expectStatus().isEqualTo(400)
//        .expectBody()
//        .jsonPath("$.error").isEqualTo("Duplicate Entry");
//
//    }

//    @Test
//    void updateFlight_ConcurrencyError_ShouldReturnConflict() {
//    	Mockito.when(flightService.addFlight(any()))
//        .thenThrow(new OptimisticLockingFailureException("Version mismatch"));
//
//    	webTestClient.post()
//        .uri("/flight/airline/inventory")
//        .contentType(MediaType.APPLICATION_JSON)
//        .bodyValue(validFlightRequest())
//        .exchange()
//        .expectStatus().isEqualTo(400)
//        .expectBody()
//        .jsonPath("$.message")
//        .isEqualTo("The data has been updated by another user. Please refresh and try again.");
//
//    }

//    @Test
//    void searchFlight_GenericAirportError_ShouldReturn400() {
//        Mockito.when(flightService.searchFlights(any()))
//                .thenThrow(new AirportException("Airport service unavailable"));
//
//        String json = """
//            {
//                "fromPlace": "DEL",
//                "toPlace": "BOM",
//                "journeyDate": "2025-12-25"
//            }
//        """;
//
//        webTestClient.post()
//                .uri("/flight/search")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(json)
//                .exchange()
//                .expectStatus().isBadRequest()
//                .expectBody()
//                .jsonPath("$.message").isEqualTo("Airport service unavailable")
//                .jsonPath("$.error").isEqualTo("Airport Error");
//    }
    private Map<String, Object> validFlightRequest() {
        return Map.of(
            "airlineName", "Indigo",
            "fromPlace", "DEL",
            "toPlace", "BOM",
            "flightDateTime", "2025-12-25T10:00:00",
            "price", 5000,
            "totalSeats", 100
        );
    }

}

