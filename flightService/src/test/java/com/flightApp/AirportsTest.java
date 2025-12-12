package com.flightApp;


import com.flightApp.model.Airports;
import com.flightApp.validation.InvalidAirportCodeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AirportsTest {

    @Test
    void fromValue_ValidCode_Success() {
        Assertions.assertEquals(Airports.DEL, Airports.fromValue("DEL"));
        Assertions.assertEquals(Airports.DEL, Airports.fromValue("del"));
    }

    @Test
    void fromValue_ValidCity_Success() {
        Assertions.assertEquals(Airports.DEL, Airports.fromValue("Delhi"));
        Assertions.assertEquals(Airports.DEL, Airports.fromValue("New Delhi"));
    }

    @Test
    void fromValue_Invalid_ThrowsException() {
        Assertions.assertThrows(InvalidAirportCodeException.class, () -> {
            Airports.fromValue("Mars");
        });
    }
    
    @Test
    void fromValue_Null_ReturnsNull() {
        Assertions.assertNull(Airports.fromValue(null));
    }
}
