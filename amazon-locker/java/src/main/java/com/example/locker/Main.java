package com.example.locker;

import com.example.locker.factory.LockerFactory;
import com.example.locker.model.DeliveryPackage;
import com.example.locker.model.PackageSize;
import com.example.locker.service.AmazonLockerService;
import com.example.locker.service.LockerLocation;
import com.example.locker.strategy.SmallestFitAssignmentStrategy;

/** Runnable demo showing deliver -> pickup with the smallest-fit assignment strategy. */
public class Main {

    public static void main(String[] args) {
        LockerLocation location = LockerLocation.of("LOC1", 2, 2, 1);
        AmazonLockerService service = new AmazonLockerService(
                location,
                new SmallestFitAssignmentStrategy());

        System.out.println("Free lockers at open: " + service.availableLockers());

        DeliveryPackage small = LockerFactory.createPackage("PKG-S", PackageSize.SMALL);
        DeliveryPackage medium = LockerFactory.createPackage("PKG-M", PackageSize.MEDIUM);
        DeliveryPackage large = LockerFactory.createPackage("PKG-L", PackageSize.LARGE);

        String c1 = service.deliver(small);
        String c2 = service.deliver(medium);
        String c3 = service.deliver(large);
        System.out.println("Delivered small package to " + service.findLocker(c1).orElseThrow().getId());
        System.out.println("Delivered medium package to " + service.findLocker(c2).orElseThrow().getId());
        System.out.println("Delivered large package to " + service.findLocker(c3).orElseThrow().getId());
        System.out.println("Free lockers now: " + service.availableLockers());

        DeliveryPackage picked = service.pickup(c2);
        System.out.println("Picked up package " + picked.getId());
        System.out.println("Free lockers after pickup: " + service.availableLockers());
    }
}
