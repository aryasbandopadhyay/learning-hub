# Airline Reservation — LLD Machine Coding (Python)

An end-to-end MVP of an airline reservation system, built for an SDE2 machine-coding round. It
focuses on flight search, seat inventory, PNR booking/cancellation, and **thread-safe** booking with
no double-booked seat.

> A parallel Java implementation lives in `../java` with its own README. The class structure is
> intentionally 1:1 between the two languages.

---

## 1. Why this MVP?

A machine-coding interviewer looks for clean OOP, correct invariants, testability, and a focused
scope. This MVP is the smallest airline system that still demonstrates the important design problem:
**seat inventory locking**.

**In scope**
- Flights with `flight_number`, route, departure time, and a fixed seat map
- `search_flights(origin, destination, optional_date, only_with_seats)`
- `book_seat(flight_no, seat_no, passenger)` for a specific seat
- `book_any(flight_no, cabin, passenger)` for first available cabin seat
- `cancel(pnr)` frees the seat
- Fixed cabin pricing via `CabinPricingStrategy`
- Thread-safe concurrent booking

**Deliberately out of scope**: payments, full fare buckets, multi-leg itineraries, waitlists,
loyalty, refunds policy, persistence, REST/UI. These are extension points, not required for proving
the core invariant.

---

## 2. UML

### Class diagram
```mermaid
classDiagram
    class Cabin {
      <<enum>>
      ECONOMY
      BUSINESS
    }
    class SeatStatus {
      <<enum>>
      AVAILABLE
      BOOKED
    }
    class Passenger
    class Seat {
      -SeatStatus _status
      -Lock _lock
      +try_book(Passenger) bool
      +free()
      +is_available bool
    }
    class FlightInventory {
      +find_seat(seat_no) Seat
      +try_book_seat(seat_no, passenger) Seat
      +try_book_any(cabin, passenger) Seat
      +available_count() int
    }
    class Flight {
      +flight_number
      +origin
      +destination
      +departure_time
      +matches(origin, destination, date) bool
    }
    class Booking {
      +pnr
      +flight_number
      +seat_no
      +passenger
      +price
    }
    class AirlineReservationService {
      +search_flights(...)
      +book_seat(...)
      +book_any(...)
      +cancel(pnr)
    }
    class CabinPricingStrategy {
      <<abstract>>
      +price_for(Cabin) int
    }
    class FixedCabinPricingStrategy

    CabinPricingStrategy <|-- FixedCabinPricingStrategy
    AirlineReservationService --> CabinPricingStrategy
    AirlineReservationService o-- Flight
    Flight o-- FlightInventory
    FlightInventory o-- Seat
    Seat --> Cabin
    Seat --> SeatStatus
    Booking --> Passenger
```

### Seat state diagram
```mermaid
stateDiagram-v2
    [*] --> AVAILABLE
    AVAILABLE --> BOOKED: try_book(passenger) wins lock
    BOOKED --> AVAILABLE: cancel(pnr) / free()
    BOOKED --> BOOKED: try_book(passenger) loses -> reject
```

### Booking sequence
```mermaid
sequenceDiagram
    participant C as Client
    participant S as AirlineReservationService
    participant I as FlightInventory
    participant Seat as Seat
    C->>S: book_seat(flight_no, seat_no, passenger)
    S->>I: find_seat(seat_no)
    S->>Seat: try_book(passenger) «with lock»
    alt seat available
        Seat-->>S: True
        S->>S: create Booking/PNR + store
        S-->>C: Booking
    else already booked
        Seat-->>S: False
        S-->>C: SeatAlreadyBookedError
    end
```

---

## 3. Design decisions

| Decision | Reasoning |
| --- | --- |
| **Flight owns FlightInventory** | Keeps route metadata separate from mutable seat state. |
| **Seat is the lock boundary** | Highest concurrency: different seats can book in parallel, same seat is serialized. |
| **Two-state `SeatStatus`** | MVP needs only `AVAILABLE` and `BOOKED`; `RESERVED` can be added later. |
| **PNR in dict + lock** | Thread-safe lookup/insert; locked `pop` makes cancellation safe. |
| **`book_any` scans then `try_book`s** | The scan is not trusted; only the atomic seat claim proves ownership. |
| **Pricing strategy** | Fare policy changes without touching booking/cancellation logic. |

### Concurrency model (the key part)
`Seat.try_book` holds a `threading.Lock`, so checking `AVAILABLE` and marking `BOOKED` is one
atomic step. When 50 threads race for `12A`, exactly one creates a PNR; the rest get
`SeatAlreadyBookedError`. Cancellation uses a locked dict `pop`, so a PNR frees its seat at most
once.

---

## 4. Code flow

```
main → AirlineReservationService.search_flights
main → AirlineReservationService.book_seat
        → FlightInventory.find_seat → Seat.try_book (atomic)
        → Booking/PNR → bookings dict
AirlineReservationService.book_any
        → FlightInventory.try_book_any → Seat.try_book (atomic)
AirlineReservationService.cancel
        → bookings.pop(pnr) under lock → Seat.free
```

Module layout:
```
airline/
├── models.py       Cabin, SeatStatus, Passenger, Seat, FlightInventory, Flight, Booking
├── strategies.py   CabinPricingStrategy + FixedCabinPricingStrategy
├── service.py      AirlineReservationService
├── exceptions.py   Flight/seat/booking domain errors
└── main.py         runnable demo
tests/
└── test_airline.py
```

---

## 5. How to run

Prerequisites: Python 3.10+ and pytest.

```powershell
cd python

# install the test runner (only dependency)
python -m pip install pytest

# run the test suite (5 tests incl. the concurrency race test)
python -m pytest -q

# run the demo
python -m airline.main
```

Expected demo output (PNR values vary):
```
Search BLR -> DEL:
AI101 BLR->DEL seats=3
Booked PNR-........ for Alice on 1A price=12000
Second booking rejected: Seat 1A is already booked
Cancelled PNR-........; seat 1A available=true
Rebooked PNR-........ for Bob on 1A
```

---

## 6. Tests

`tests/test_airline.py` covers:
- route/date search and `only_with_seats` filtering
- specific seat booking creates a PNR and marks the seat BOOKED
- duplicate specific-seat booking is rejected
- `book_any` picks the requested cabin and rejects when that cabin is full
- cancel frees the seat and a consumed PNR cannot be cancelled twice
- **concurrency**: 50 threads race for the same seat → exactly 1 success, 0 double-booking

---

## 7. Extending
- **Payments**: collect payment after a PNR is created, or hold then confirm.
- **Fares/pricing tiers**: replace `FixedCabinPricingStrategy` with dynamic fare buckets.
- **Multi-leg itineraries**: introduce `Itinerary` and atomically reserve seats across flights.
- **Waitlists/holds**: add `RESERVED` state with expiry.
- **Persistence**: move flights/bookings behind repositories.
