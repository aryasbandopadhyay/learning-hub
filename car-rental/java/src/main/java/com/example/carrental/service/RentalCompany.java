package com.example.carrental.service;

import com.example.carrental.exception.CarUnavailableException;
import com.example.carrental.exception.InvalidDateRangeException;
import com.example.carrental.exception.InvalidReservationStateException;
import com.example.carrental.exception.ReservationNotFoundException;
import com.example.carrental.model.Car;
import com.example.carrental.model.CarType;
import com.example.carrental.model.Reservation;
import com.example.carrental.model.ReservationStatus;
import com.example.carrental.strategy.PricingStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Application service (aggregate root): owns fleet search, booking, and reservation lifecycle.
 *
 * <p><b>Concurrency:</b> reserve(carId, range) locks that car, checks all blocking reservations,
 * calculates price, and appends the new CONFIRMED reservation while still holding the lock. The
 * check-and-insert is therefore atomic, which is the core invariant for no double-booking.
 */
public class RentalCompany {

    private final ConcurrentMap<String, Car> cars = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Reservation> reservationsById = new ConcurrentHashMap<>();
    private final PricingStrategy pricingStrategy;

    public RentalCompany(List<Car> initialCars, PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
        initialCars.forEach(this::addCar);
    }

    public void addCar(Car car) {
        cars.put(car.getId(), car);
    }

    public List<Car> searchAvailable(String location, CarType type, LocalDate pickup, LocalDate returnDate) {
        validateRange(pickup, returnDate);
        List<Car> result = new ArrayList<>();
        for (Car car : cars.values()) {
            if (!car.getLocation().equals(location) || car.getType() != type) {
                continue;
            }
            car.getLock().lock();
            try {
                if (isAvailableLocked(car, pickup, returnDate)) {
                    result.add(car);
                }
            } finally {
                car.getLock().unlock();
            }
        }
        return result;
    }

    public Reservation reserve(String carId, LocalDate pickup, LocalDate returnDate) {
        validateRange(pickup, returnDate);
        Car car = Optional.ofNullable(cars.get(carId))
                .orElseThrow(() -> new CarUnavailableException("Unknown car: " + carId));

        car.getLock().lock();
        try {
            if (!isAvailableLocked(car, pickup, returnDate)) {
                throw new CarUnavailableException("Car unavailable for requested dates: " + carId);
            }
            long total = pricingStrategy.calculatePrice(car, pickup, returnDate);
            Reservation reservation = new Reservation(car, pickup, returnDate, total);
            car.getReservations().add(reservation);
            reservationsById.put(reservation.getId(), reservation);
            return reservation;
        } finally {
            car.getLock().unlock();
        }
    }

    public Reservation pickUp(String reservationId) {
        return transition(reservationId, ReservationStatus.CONFIRMED, ReservationStatus.PICKED_UP);
    }

    public Reservation returnCar(String reservationId) {
        return transition(reservationId, ReservationStatus.PICKED_UP, ReservationStatus.RETURNED);
    }

    public Reservation cancel(String reservationId) {
        return transition(reservationId, ReservationStatus.CONFIRMED, ReservationStatus.CANCELLED);
    }

    public List<Reservation> reservationsForCar(String carId) {
        Car car = cars.get(carId);
        if (car == null) {
            return List.of();
        }
        car.getLock().lock();
        try {
            return List.copyOf(car.getReservations());
        } finally {
            car.getLock().unlock();
        }
    }

    private Reservation transition(String reservationId,
                                   ReservationStatus expected,
                                   ReservationStatus next) {
        Reservation reservation = Optional.ofNullable(reservationsById.get(reservationId))
                .orElseThrow(() -> new ReservationNotFoundException("Unknown reservation: " + reservationId));
        Car car = reservation.getCar();
        car.getLock().lock();
        try {
            if (reservation.getStatus() != expected) {
                throw new InvalidReservationStateException(
                        "Expected " + expected + " but was " + reservation.getStatus());
            }
            reservation.setStatus(next);
            return reservation;
        } finally {
            car.getLock().unlock();
        }
    }

    private boolean isAvailableLocked(Car car, LocalDate pickup, LocalDate returnDate) {
        return car.getReservations().stream()
                .filter(Reservation::blocksAvailability)
                .noneMatch(r -> r.overlaps(pickup, returnDate));
    }

    private static void validateRange(LocalDate pickup, LocalDate returnDate) {
        if (!pickup.isBefore(returnDate)) {
            throw new InvalidDateRangeException("pickup must be before return");
        }
    }
}
