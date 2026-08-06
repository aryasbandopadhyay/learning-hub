package com.example.bookmyshow.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Theater {
    private final String id;
    private final String name;
    private final List<Screen> screens = new ArrayList<>();

    public Theater(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addScreen(Screen screen) {
        screens.add(screen);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Screen> getScreens() {
        return Collections.unmodifiableList(screens);
    }
}
