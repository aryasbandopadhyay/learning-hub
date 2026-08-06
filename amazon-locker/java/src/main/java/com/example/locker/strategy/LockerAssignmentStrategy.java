package com.example.locker.strategy;

import com.example.locker.model.DeliveryPackage;
import com.example.locker.model.Locker;
import com.example.locker.service.LockerLocation;

import java.util.Optional;

/** Strategy pattern: decides WHICH locker gets a package and atomically reserves it. */
public interface LockerAssignmentStrategy {

    /**
     * Find and atomically reserve a compatible free locker.
     *
     * @return the reserved locker, or empty if the location is full for this package size.
     */
    Optional<Locker> assign(LockerLocation location, DeliveryPackage pkg);
}
