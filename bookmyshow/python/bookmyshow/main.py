"""Runnable demo.

Run from ``bookmyshow/python``:
    python -m bookmyshow.main
"""

from __future__ import annotations

from datetime import datetime, timedelta, timezone

from .models import City, Movie, Screen, Seat, Show, Theater
from .service import BookMyShowService


def main() -> None:
    service = BookMyShowService(hold_duration=timedelta(minutes=5))
    service.add_city(sample_city())

    shows = service.search_shows("Bengaluru", "Interstellar")
    print("Shows found for Interstellar in Bengaluru:", len(shows))

    hold = service.hold_seats("show-1", ["A1", "A2"], "user-1")
    print(f"Held seats {list(hold.seat_ids)} until {hold.expires_at.isoformat()}")

    booking = service.confirm_booking(hold.id, "pay-123")
    print(f"Booking confirmed: {booking.id} seats={list(booking.seat_ids)}")


def sample_city() -> City:
    interstellar = Movie("movie-1", "Interstellar")
    inception = Movie("movie-2", "Inception")
    show1 = Show("show-1", interstellar, datetime(2026, 8, 5, 18, tzinfo=timezone.utc), seats("A", 5))
    show2 = Show("show-2", inception, datetime(2026, 8, 5, 21, tzinfo=timezone.utc), seats("B", 5))
    screen = Screen("screen-1", "Audi 1")
    screen.add_show(show1)
    screen.add_show(show2)
    theater = Theater("theater-1", "PVR Orion")
    theater.add_screen(screen)
    city = City("city-1", "Bengaluru")
    city.add_theater(theater)
    return city


def seats(row: str, count: int) -> list[Seat]:
    return [Seat(row, i) for i in range(1, count + 1)]


if __name__ == "__main__":
    main()

