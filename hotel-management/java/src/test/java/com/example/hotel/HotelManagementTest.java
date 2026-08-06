package com.example.hotel;

import com.example.hotel.exception.RoomUnavailableException;
import com.example.hotel.model.Hotel;
import com.example.hotel.model.Reservation;
import com.example.hotel.model.ReservationStatus;
import com.example.hotel.model.Room;
import com.example.hotel.model.RoomType;
import com.example.hotel.service.HotelManagementService;
import com.example.hotel.strategy.NightlyPricingStrategy;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HotelManagementTest {

    private HotelManagementService newService() {
        Hotel hotel = new Hotel("Test Hotel", List.of(
                new Room("101", RoomType.STANDARD),
                new Room("102", RoomType.STANDARD),
                new Room("201", RoomType.DELUXE),
                new Room("301", RoomType.SUITE)));
        return new HotelManagementService(hotel, new NightlyPricingStrategy());
    }

    @Test
    void searchReturnsOnlyRoomsFreeForTheRange() {
        HotelManagementService service = newService();
        LocalDate in = LocalDate.of(2026, 1, 10);
        LocalDate out = LocalDate.of(2026, 1, 12);

        service.bookRoom("101", in, out);

        List<String> availableIds = service.searchAvailableRooms(RoomType.STANDARD, in, out)
                .stream().map(Room::getId).toList();
        assertEquals(List.of("102"), availableIds);
    }

    @Test
    void adjacentBookingsAreAllowedButTrueOverlapsAreExcluded() {
        HotelManagementService service = newService();
        service.bookRoom("101", LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 12));

        assertTrue(service.searchAvailableRooms(
                RoomType.STANDARD, LocalDate.of(2026, 1, 12), LocalDate.of(2026, 1, 14))
                .stream().anyMatch(room -> room.getId().equals("101")));
        assertTrue(service.searchAvailableRooms(
                RoomType.STANDARD, LocalDate.of(2026, 1, 11), LocalDate.of(2026, 1, 13))
                .stream().noneMatch(room -> room.getId().equals("101")));
    }

    @Test
    void bookComputesTotalAndUnavailableRoomThrows() {
        HotelManagementService service = newService();
        LocalDate in = LocalDate.of(2026, 2, 1);
        LocalDate out = LocalDate.of(2026, 2, 4);

        Reservation reservation = service.bookRoom("201", in, out); // 3 DELUXE nights * 180
        assertEquals(540L, reservation.getTotalPrice());
        assertThrows(RoomUnavailableException.class, () -> service.bookRoom("201", in.plusDays(1), out.plusDays(1)));
    }

    @Test
    void lifecycleAndCancelFreeDates() {
        HotelManagementService service = newService();
        LocalDate in = LocalDate.of(2026, 3, 1);
        LocalDate out = LocalDate.of(2026, 3, 3);

        Reservation stay = service.bookRoom("101", in, out);
        service.checkIn(stay.getId());
        assertEquals(ReservationStatus.CHECKED_IN, stay.getStatus());
        service.checkOut(stay.getId());
        assertEquals(ReservationStatus.CHECKED_OUT, stay.getStatus());

        Reservation cancelled = service.bookRoom("102", in, out);
        service.cancel(cancelled.getId());
        assertEquals(ReservationStatus.CANCELLED, cancelled.getStatus());
        Reservation replacement = service.bookRoom("102", in, out);
        assertEquals(ReservationStatus.CONFIRMED, replacement.getStatus());
    }

    /** Many threads race to book the same room/date range; the per-room lock must allow one winner. */
    @Test
    void concurrentBookingNeverDoubleBooksSameRoomRange() throws InterruptedException {
        HotelManagementService service = newService();
        LocalDate in = LocalDate.of(2026, 4, 1);
        LocalDate out = LocalDate.of(2026, 4, 5);
        int threads = 50;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        ConcurrentLinkedQueue<String> reservationIds = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    Reservation reservation = service.bookRoom("101", in, out);
                    successes.incrementAndGet();
                    reservationIds.add(reservation.getId());
                } catch (RoomUnavailableException ignored) {
                    // expected for the losers
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(1, successes.get(), "exactly one thread may reserve the room");
        assertEquals(1, reservationIds.stream().distinct().count(), "only one reservation id is created");
        assertEquals(1, service.activeOverlappingReservations("101", in, out), "no overlapping confirmed reservations exist");
    }
}
