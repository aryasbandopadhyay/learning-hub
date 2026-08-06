package com.example.locker.factory;

import com.example.locker.model.DeliveryPackage;
import com.example.locker.model.Locker;
import com.example.locker.model.LockerSize;
import com.example.locker.model.PackageSize;

/** Factory pattern: callers depend on simple enum values, not concrete constructors. */
public final class LockerFactory {

    private LockerFactory() {
    }

    public static DeliveryPackage createPackage(String id, PackageSize size) {
        return new DeliveryPackage(id, size);
    }

    public static Locker createLocker(String id, LockerSize size) {
        return new Locker(id, size);
    }
}
