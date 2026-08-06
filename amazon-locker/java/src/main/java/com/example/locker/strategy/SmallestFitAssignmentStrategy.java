package com.example.locker.strategy;

import com.example.locker.model.DeliveryPackage;
import com.example.locker.model.Locker;
import com.example.locker.service.LockerLocation;

import java.util.Comparator;
import java.util.Optional;

/**
 * Smallest-fit assignment: try SMALL lockers first, then MEDIUM, then LARGE. This preserves larger
 * lockers for larger packages while still relying on Locker.tryOccupy for thread-safety.
 */
public class SmallestFitAssignmentStrategy implements LockerAssignmentStrategy {

    @Override
    public Optional<Locker> assign(LockerLocation location, DeliveryPackage pkg) {
        return location.getLockers().stream()
                .sorted(Comparator.comparing(Locker::getSize).thenComparing(Locker::getId))
                .filter(locker -> locker.tryOccupy(pkg))
                .findFirst();
    }
}
