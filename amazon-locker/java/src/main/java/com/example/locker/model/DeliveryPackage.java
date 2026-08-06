package com.example.locker.model;

/** Immutable package request. In production this would also carry customer/order metadata. */
public class DeliveryPackage {

    private final String id;
    private final PackageSize size;

    public DeliveryPackage(String id, PackageSize size) {
        this.id = id;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public PackageSize getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Package(" + id + ", " + size + ")";
    }
}
