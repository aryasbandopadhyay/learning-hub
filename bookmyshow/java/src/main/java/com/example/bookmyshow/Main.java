package com.example.bookmyshow;

import com.example.bookmyshow.model.Booking;
import com.example.bookmyshow.model.City;
import com.example.bookmyshow.model.Movie;
import com.example.bookmyshow.model.Screen;
import com.example.bookmyshow.model.Seat;
import com.example.bookmyshow.model.SeatHold;
import com.example.bookmyshow.model.Show;
import com.example.bookmyshow.model.Theater;
import com.example.bookmyshow.service.BookMyShowService;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BookMyShowService service = new BookMyShowService(Clock.systemUTC(), Duration.ofMinutes(5));
        service.addCity(sampleCity());

        List<Show> shows = service.searchShows("Bengaluru", "Interstellar");
        System.out.println("Shows found for Interstellar in Bengaluru: " + shows.size());

        SeatHold hold = service.holdSeats("show-1", List.of("A1", "A2"), "user-1");
        System.out.println("Held seats " + hold.seatIds() + " until " + hold.expiresAt());

        Booking booking = service.confirmBooking(hold.id(), "pay-123");
        System.out.println("Booking confirmed: " + booking.id() + " seats=" + booking.seatIds());
    }

    private static City sampleCity() {
        Movie interstellar = new Movie("movie-1", "Interstellar");
        Movie inception = new Movie("movie-2", "Inception");
        Show show1 = new Show("show-1", interstellar, Instant.parse("2026-08-05T18:00:00Z"), seats("A", 5));
        Show show2 = new Show("show-2", inception, Instant.parse("2026-08-05T21:00:00Z"), seats("B", 5));
        Screen screen = new Screen("screen-1", "Audi 1");
        screen.addShow(show1);
        screen.addShow(show2);
        Theater theater = new Theater("theater-1", "PVR Orion");
        theater.addScreen(screen);
        City city = new City("city-1", "Bengaluru");
        city.addTheater(theater);
        return city;
    }

    private static List<Seat> seats(String row, int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> new Seat(row, i))
                .toList();
    }
}
