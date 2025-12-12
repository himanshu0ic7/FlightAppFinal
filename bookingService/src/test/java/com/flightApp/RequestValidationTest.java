package com.flightApp;


import com.flightApp.dto.BookingRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        req.setEmailId("valid@test.com");
        req.setName("John Doe");
        req.setMobileNumber("9876543210");
        com.flightApp.dto.PassengerDTO p = new com.flightApp.dto.PassengerDTO();
        p.setName("Passenger 1");
        p.setAge(30);
        p.setPassengerId("PID12345");
        p.setGender(com.flightApp.model.Gender.MALE);
        java.util.List<com.flightApp.dto.PassengerDTO> list = new java.util.ArrayList<>();
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
    void testInvalidEmail() {
        BookingRequest req = new BookingRequest();
        req.setFlightId("F100");
        req.setNumberOfSeats(1);
        req.setEmailId("invalid-email"); 
        req.setName("John");
        req.setMobileNumber("9876543210");

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(req);
        
        assertFalse(violations.isEmpty());
        boolean hasEmailError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("emailId"));
        assertTrue(hasEmailError, "Expected Email validation error");
    }

    @Test
    void testInvalidSeats() {
        BookingRequest req = new BookingRequest();
        req.setFlightId("F100");
        req.setNumberOfSeats(0);
        req.setEmailId("test@test.com");
        req.setName("John");
        req.setMobileNumber("9876543210");

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(req);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("numberOfSeats")));
    }
}
