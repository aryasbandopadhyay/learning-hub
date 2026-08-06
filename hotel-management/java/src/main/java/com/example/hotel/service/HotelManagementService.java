package com.example.hotel.service;

import com.example.hotel.exception.ReservationNotFoundException;
import com.example.hotel.exception.RoomNotFoundException;
import com.example.hotel.model.Hotel;
import com.example.hotel.model.Reservation;
import com.example.hotel.model.ReservationStatus;
import com.example.hotel.model.Room;
import com.example.hotel.model.RoomType;
import com.example.hotel.strategy.PricingStrategy;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Application service for the booking workflow.
 *
 * <p>Search is a read-only snapshot over rooms of a type. Booking a specific room delegates to the
 * room's per-room lock, so the critical section is tiny: check overlaps + append reservation. The
 * reservation map is concurrent so lifecycle operations can find reservations safely by id.
 */
public class HotelManagementService {

    private final Hotel hotel;
    private final PricingStrategy pricingStrategy;
    private final ConcurrentMap<String, Reservation> reservationsById = new ConcurrentHashMap<>();

    public HotelManagementService(Hotel hotel, PricingStrategy pricingStrategy) {
        this.hotel = hotel;
        this.pricingStrategy = pricingStrategy;
    }

    public List<Room> searchAvailableRooms(RoomType type, LocalDate checkIn, LocalDate checkOut) {
        return hotel.getRooms().stream()
                .filter(room -> room.getRoomType() == type)
                .filter(room -> room.isAvailable(checkIn, checkOut))
                .sorted(Comparator.comparing(Room::getId))
                .toList();
    }

    public Reservation bookRoom(String roomId, LocalDate checkIn, LocalDate checkOut) {
        Room room = findRoom(roomId).orElseThrow(() -> new RoomNotFoundException("Room not found: " + roomId));
        Reservation reservation = room.book(checkIn, checkOut, pricingStrategy);
        reservationsById.put(reservation.getId(), reservation);
        return reservation;
    }

    public Reservation checkIn(String reservationId) {
        Reservation reservation = findReservationOrThrow(reservationId);
        reservation.checkIn();
        return reservation;
    }

    public Reservation checkOut(String reservationId) {
        Reservation reservation = findReservationOrThrow(reservationId);
        reservation.checkOut();
        return reservation;
    }

    public Reservation cancel(String reservationId) {
        Reservation reservation = findReservationOrThrow(reservationId);
        reservation.cancel();
        return reservation;
    }

    public Optional<Reservation> findReservation(String reservationId) {
        return Optional.ofNullable(reservationsById.get(reservationId));
    }

    public List<Reservation> reservationsForRoom(String roomId) {
        return findRoom(roomId)
                .map(Room::getReservationsSnapshot)
                .orElseThrow(() -> new RoomNotFoundException("Room not found: " + roomId));
    }

    public long activeOverlappingReservations(String roomId, LocalDate checkIn, LocalDate checkOut) {
        return reservationsForRoom(roomId).stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED || r.getStatus() == ReservationStatus.CHECKED_IN)
                .filter(r -> r.overlaps(checkIn, checkOut))
                .count();
    }

    private Reservation findReservationOrThrow(String reservationId) {
        Reservation reservation = reservationsById.get(reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation not found: " + reservationId);
        }
        return reservation;
    }

    private Optional<Room> findRoom(String roomId) {
        return hotel.getRooms().stream().filter(room -> room.getId().equals(roomId)).findFirst();
    }
}
