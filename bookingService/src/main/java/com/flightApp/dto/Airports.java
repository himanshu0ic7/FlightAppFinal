package com.flightApp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.flightApp.validation.InvalidAirportCodeException;

import lombok.Getter;

@Getter
public enum Airports {
    DEL("New Delhi", "Indira Gandhi International Airport"),
    BOM("Mumbai", "Chhatrapati Shivaji Maharaj International Airport"),
    BLR("Bengaluru", "Kempegowda International Airport"),
    HYD("Hyderabad", "Rajiv Gandhi International Airport"),
    MAA("Chennai", "Chennai International Airport"),
    CCU("Kolkata", "Netaji Subhash Chandra Bose International Airport"),
    AMD("Ahmedabad", "Sardar Vallabhbhai Patel International Airport"),
    PNQ("Pune", "Pune Airport"),
    GOI("Goa", "Dabolim Airport"),
    COK("Kochi", "Cochin International Airport"),
    JAI("Jaipur", "Jaipur International Airport"),
    LKO("Lucknow", "Chaudhary Charan Singh International Airport"),
    TRV("Thiruvananthapuram", "Trivandrum International Airport"),
    GAU("Guwahati", "Lokpriya Gopinath Bordoloi International Airport"),
    BBI("Bhubaneswar", "Biju Patnaik International Airport");

    private final String city;
    private final String airportName;

    Airports(String city, String airportName) {
        this.city = city;
        this.airportName = airportName;
    }
    
    @JsonCreator
    public static Airports fromValue(String value) {
        for (Airports code : values()) {
            if (code.name().equalsIgnoreCase(value)) {
                return code;
            }
        }
        throw new InvalidAirportCodeException("Enter correct airport code. Invalid value: " + value);
    }
}
