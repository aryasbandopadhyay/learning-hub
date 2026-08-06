package com.example.locker.service;

import com.example.locker.exception.InvalidPickupCodeException;
import com.example.locker.exception.NoAvailableLockerException;
import com.example.locker.model.DeliveryPackage;
import com.example.locker.model.Locker;
import com.example.locker.strategy.LockerAssignmentStrategy;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Application service (aggregate root) that ties the model + strategy together.
 *
 * <p>Concurrency: allocation is per-locker atomic via Locker.tryOccupy. Pickup codes live in a
 * ConcurrentHashMap; atomic remove guarantees a code is consumed exactly once.
 */
public class AmazonLockerService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final LockerLocation location;
    private final LockerAssignmentStrategy assignmentStrategy;
    private final ConcurrentMap<String, Locker> codeToLocker = new ConcurrentHashMap<>();

    public AmazonLockerService(LockerLocation location, LockerAssignmentStrategy assignmentStrategy) {
        this.location = location;
        this.assignmentStrategy = assignmentStrategy;
    }

    /** Deliver a package: reserve the smallest fitting locker and return a pickup code. */
    public String deliver(DeliveryPackage pkg) {
        Locker locker = assignmentStrategy.assign(location, pkg)
                .orElseThrow(() -> new NoAvailableLockerException(
                        "No available locker for " + pkg));
        return storeWithFreshPickupCode(locker);
    }

    /** Pickup consumes the code, opens the locker, frees it, and returns the package. */
    public DeliveryPackage pickup(String code) {
        Locker locker = codeToLocker.remove(code);
        if (locker == null) {
            throw new InvalidPickupCodeException("Unknown or already-used pickup code: " + code);
        }
        return locker.free();
    }

    private String storeWithFreshPickupCode(Locker locker) {
        while (true) {
            String code = String.format("%06d", RANDOM.nextInt(1_000_000));
            if (codeToLocker.putIfAbsent(code, locker) == null) {
                return code;
            }
        }
    }

    public long availableLockers() {
        return location.availableCount();
    }

    public Optional<Locker> findLocker(String code) {
        return Optional.ofNullable(codeToLocker.get(code));
    }

    public LockerLocation getLocation() {
        return location;
    }
}
