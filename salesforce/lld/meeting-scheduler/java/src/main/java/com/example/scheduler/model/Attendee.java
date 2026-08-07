package com.example.scheduler.model;

import java.util.Objects;

/** Simple value object for a participant. Kept small because persistence/invites are out of scope. */
public record Attendee(String email) {
    public Attendee {
        Objects.requireNonNull(email, "email");
        if (email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
    }
}
