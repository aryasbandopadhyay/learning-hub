package com.example.locker.model;

/**
 * A single locker. THIS CLASS IS THE CONCURRENCY BOUNDARY.
 *
 * <p>{@link #tryOccupy(DeliveryPackage)} and {@link #free()} are {@code synchronized}, so the
 * check "is this locker free and can this package fit?" and the state change happen as one atomic
 * step. When many threads race for the last free locker, exactly one flips the state to OCCUPIED.
 */
public class Locker {

    private final String id;
    private final LockerSize size;

    // Guarded by 'this' monitor (synchronized methods).
    private LockerState state = LockerState.FREE;
    private DeliveryPackage currentPackage;

    public Locker(String id, LockerSize size) {
        this.id = id;
        this.size = size;
    }

    /** A package fits if the locker is at least as large as the package. */
    public boolean canFit(DeliveryPackage pkg) {
        return size.ordinal() >= pkg.getSize().ordinal();
    }

    /** Atomically place the package here; return true only for the winning thread. */
    public synchronized boolean tryOccupy(DeliveryPackage pkg) {
        if (state != LockerState.FREE || !canFit(pkg)) {
            return false;
        }
        this.state = LockerState.OCCUPIED;
        this.currentPackage = pkg;
        return true;
    }

    /** Atomically open the locker, remove the package, and make the locker reusable. */
    public synchronized DeliveryPackage free() {
        DeliveryPackage pkg = currentPackage;
        this.currentPackage = null;
        this.state = LockerState.FREE;
        return pkg;
    }

    public synchronized boolean isFree() {
        return state == LockerState.FREE;
    }

    public synchronized LockerState getState() {
        return state;
    }

    public String getId() {
        return id;
    }

    public LockerSize getSize() {
        return size;
    }
}
