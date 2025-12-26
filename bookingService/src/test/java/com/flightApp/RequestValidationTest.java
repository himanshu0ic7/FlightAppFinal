package com.flightApp;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.flightApp.dtos.BookingRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidBookingRequest() {
        BookingRequest req = new BookingRequest();
        req.setFlightId("F100");
        req.setNumberOfSeats(1);
        req.setName("John Doe");
        com.flightApp.dtos.PassengerDTO p = new com.flightApp.dtos.PassengerDTO();
        p.setName("Passenger 1");
        p.setAge(30);
        p.setPassengerId("PID12345");
        p.setGender(com.flightApp.model.Gender.MALE);
        java.util.List<com.flightApp.dtos.PassengerDTO> list = new java.util.ArrayList<>();
        list.add(p);
        req.setPassengers(list);

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(req);
      
        if (!violations.isEmpty()) {
            System.out.println("TEST FAILURE DIAGNOSIS:");
            for (ConstraintViolation<BookingRequest> v : violations) {
                System.out.println("   Field: [" + v.getPropertyPath() + "]");
                System.out.println("   Error: " + v.getMessage());
                System.out.println("   Value: " + v.getInvalidValue());
                System.out.println("   -----------------");
            }
        }

        assertTrue(violations.isEmpty(), "Expected no validation errors, but found: " + violations.size());
    }

    @Test
    void testInvalidSeats() {
        BookingRequest req = new BookingRequest();
        req.setFlightId("F100");
        req.setNumberOfSeats(0);
        req.setName("John");

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(req);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("numberOfSeats")));
    }
}
