package com.example.hotel;

import com.example.hotel.model.Hotel;
import com.example.hotel.model.Reservation;
import com.example.hotel.model.Room;
import com.example.hotel.model.RoomType;
import com.example.hotel.service.HotelManagementService;
import com.example.hotel.strategy.NightlyPricingStrategy;

import java.time.LocalDate;
import java.util.List;

/** Runnable demo showing search -> book -> check-in -> check-out for a deterministic date range. */
public class Main {

    public static void main(String[] args) {
        Hotel hotel = new Hotel("Sea View", List.of(
                new Room("101", RoomType.STANDARD),
                new Room("102", RoomType.STANDARD),
                new Room("201", RoomType.DELUXE),
                new Room("301", RoomType.SUITE)));
        HotelManagementService service = new HotelManagementService(hotel, new NightlyPricingStrategy());

        LocalDate checkIn = LocalDate.of(2026, 1, 10);
        LocalDate checkOut = LocalDate.of(2026, 1, 12);

        System.out.println("Available STANDARD rooms for 2026-01-10 to 2026-01-12: "
                + ids(service.searchAvailableRooms(RoomType.STANDARD, checkIn, checkOut)));

        Reservation reservation = service.bookRoom("101", checkIn, checkOut);
        System.out.println("Booked room " + reservation.getRoom().getId()
                + " for 2 nights, total = " + reservation.getTotalPrice());
        System.out.println("Available STANDARD rooms after booking: "
                + ids(service.searchAvailableRooms(RoomType.STANDARD, checkIn, checkOut)));

        service.checkIn(reservation.getId());
        System.out.println("Reservation status after check-in: " + reservation.getStatus());
        service.checkOut(reservation.getId());
        System.out.println("Reservation status after check-out: " + reservation.getStatus());
    }

    private static List<String> ids(List<Room> rooms) {
        return rooms.stream().map(Room::getId).toList();
    }
}
