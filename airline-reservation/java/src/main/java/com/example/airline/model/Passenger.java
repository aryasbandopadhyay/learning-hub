package com.example.airline.model;

/** Passenger details kept minimal for the MVP; identity/KYC/loyalty are extension points. */
public record Passenger(String name, String email) {
}
