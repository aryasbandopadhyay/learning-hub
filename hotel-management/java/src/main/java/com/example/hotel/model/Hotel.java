package com.example.hotel.model;

import java.util.List;

/** Hotel aggregate: a named collection of rooms. The service owns workflows across these rooms. */
public class Hotel {

    private final String name;
    private final List<Room> rooms;

    public Hotel(String name, List<Room> rooms) {
        this.name = name;
        this.rooms = List.copyOf(rooms);
    }

    public String getName() {
        return name;
    }

    public List<Room> getRooms() {
        return rooms;
    }
}
