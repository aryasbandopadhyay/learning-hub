package com.example.locker.service;

import com.example.locker.factory.LockerFactory;
import com.example.locker.model.Locker;
import com.example.locker.model.LockerSize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A physical locker location holding a fixed set of lockers. */
public class LockerLocation {

    private final String id;
    private final List<Locker> lockers;

    public LockerLocation(String id, List<Locker> lockers) {
        this.id = id;
        this.lockers = List.copyOf(lockers);
    }

    /** Convenience factory. Locker ids look like "LOC1-L0". */
    public static LockerLocation of(String id, int small, int medium, int large) {
        List<Locker> lockers = new ArrayList<>();
        int idx = 0;
        for (int i = 0; i < small; i++) {
            lockers.add(LockerFactory.createLocker(id + "-L" + idx++, LockerSize.SMALL));
        }
        for (int i = 0; i < medium; i++) {
            lockers.add(LockerFactory.createLocker(id + "-L" + idx++, LockerSize.MEDIUM));
        }
        for (int i = 0; i < large; i++) {
            lockers.add(LockerFactory.createLocker(id + "-L" + idx++, LockerSize.LARGE));
        }
        return new LockerLocation(id, lockers);
    }

    public String getId() {
        return id;
    }

    public List<Locker> getLockers() {
        return Collections.unmodifiableList(lockers);
    }

    /** Count of currently free lockers (snapshot; useful for reporting/tests). */
    public long availableCount() {
        return lockers.stream().filter(Locker::isFree).count();
    }
}
