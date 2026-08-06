package com.example.splitwise.model;

import java.util.Objects;

/**
 * A person in the system. The immutable id is the identity, so two User objects with the same id
 * represent the same real user. This keeps maps stable and makes tests easy to read.
 */
public final class User {

    private final String id;
    private final String name;

    public User(String id, String name) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("User id is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("User name is required");
        }
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof User user)) {
            return false;
        }
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
