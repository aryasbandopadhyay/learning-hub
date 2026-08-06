package com.example.bookmyshow.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class City {
    private final String id;
    private final String name;
    private final List<Theater> theaters = new ArrayList<>();

    public City(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addTheater(Theater theater) {
        theaters.add(theater);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Theater> getTheaters() {
        return Collections.unmodifiableList(theaters);
    }
}
