package com.example.parkinglot.service;

import com.example.parkinglot.model.ParkingSpot;
import com.example.parkinglot.model.SpotType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A floor of the parking lot holding a fixed set of {@link ParkingSpot}s. The spot list is
 * immutable after construction; concurrency is handled at the spot level, so a Level needs no
 * locking of its own for park/unpark.
 */
public class Level {

    private final int levelNumber;
    private final List<ParkingSpot> spots;

    public Level(int levelNumber, List<ParkingSpot> spots) {
        this.levelNumber = levelNumber;
        this.spots = List.copyOf(spots);
    }

    /**
     * Convenience factory: build a level with the given counts of each spot size. Spot ids look
     * like "L0-S3" (level 0, spot index 3).
     */
    public static Level of(int levelNumber, int small, int medium, int large) {
        List<ParkingSpot> spots = new ArrayList<>();
        int idx = 0;
        for (int i = 0; i < small; i++) {
            spots.add(new ParkingSpot("L" + levelNumber + "-S" + idx++, SpotType.SMALL));
        }
        for (int i = 0; i < medium; i++) {
            spots.add(new ParkingSpot("L" + levelNumber + "-S" + idx++, SpotType.MEDIUM));
        }
        for (int i = 0; i < large; i++) {
            spots.add(new ParkingSpot("L" + levelNumber + "-S" + idx++, SpotType.LARGE));
        }
        return new Level(levelNumber, spots);
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public List<ParkingSpot> getSpots() {
        return Collections.unmodifiableList(spots);
    }

    /** Count of currently free spots (snapshot; useful for reporting/tests). */
    public long availableCount() {
        return spots.stream().filter(s -> !s.isOccupied()).count();
    }
}
