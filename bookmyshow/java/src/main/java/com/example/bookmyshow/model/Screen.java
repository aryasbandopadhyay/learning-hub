package com.example.bookmyshow.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Screen {
    private final String id;
    private final String name;
    private final List<Show> shows = new ArrayList<>();

    public Screen(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addShow(Show show) {
        shows.add(show);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Show> getShows() {
        return Collections.unmodifiableList(shows);
    }
}
