package com.example.parkinglot;

import com.example.parkinglot.factory.VehicleFactory;
import com.example.parkinglot.model.Ticket;
import com.example.parkinglot.model.Vehicle;
import com.example.parkinglot.model.VehicleType;
import com.example.parkinglot.service.Level;
import com.example.parkinglot.service.ParkingLot;
import com.example.parkinglot.service.Receipt;
import com.example.parkinglot.strategy.HourlyFeeStrategy;
import com.example.parkinglot.strategy.NearestFirstAssignmentStrategy;

import java.time.Clock;
import java.util.List;

/**
 * Runnable demo showing the end-to-end flow: build a lot, park a few vehicles, then unpark and
 * print receipts. Run with {@code mvn -q compile exec} or from the packaged jar / IDE.
 */
public class Main {

    public static void main(String[] args) {
        // Two levels, each with 2 small, 2 medium, 1 large spot.
        List<Level> levels = List.of(
                Level.of(0, 2, 2, 1),
                Level.of(1, 2, 2, 1));

        ParkingLot lot = new ParkingLot(
                levels,
                new NearestFirstAssignmentStrategy(),
                new HourlyFeeStrategy(),
                Clock.systemUTC());

        System.out.println("Free spots at open: " + lot.availableSpots());

        Vehicle bike = VehicleFactory.create(VehicleType.MOTORCYCLE, "KA-01-1234");
        Vehicle car = VehicleFactory.create(VehicleType.CAR, "KA-02-5678");
        Vehicle truck = VehicleFactory.create(VehicleType.TRUCK, "KA-03-9999");

        Ticket t1 = lot.park(bike);
        Ticket t2 = lot.park(car);
        Ticket t3 = lot.park(truck);
        System.out.println("Parked bike at " + t1.getSpot().getId());
        System.out.println("Parked car  at " + t2.getSpot().getId());
        System.out.println("Parked truck at " + t3.getSpot().getId());
        System.out.println("Free spots now: " + lot.availableSpots());

        Receipt r = lot.unpark(t2.getId());
        System.out.println("Car left spot " + r.ticket().getSpot().getId() + ", fee = " + r.fee());
        System.out.println("Free spots after exit: " + lot.availableSpots());
    }
}
