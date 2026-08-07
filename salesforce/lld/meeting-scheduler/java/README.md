# Meeting Scheduler — LLD Machine Coding (Java)

An end-to-end MVP of a meeting room scheduler/calendar, built for an SDE2 machine-coding round. It
demonstrates OOP modelling, the **Strategy** pattern, a sweep-line calendar utility, and
**thread-safe** concurrent booking with no overlapping meetings in the same room.

> A parallel Python implementation lives in `../python` with its own README. Both produce identical
> demo output.

---

## 1. Why this MVP?

A machine-coding interviewer looks for: clean OOP, at least one design pattern applied for a real
reason, correct concurrency, and working tests — delivered in ~45 minutes. So the MVP is the
**smallest system that still exercises all of those**:

**In scope**
- Rooms with independent calendars
- `book` → find a free room for a requested [start, end) interval → return `Booking`
- `cancel` → remove the booking and free that interval
- Query bookings for a room/day
- Meeting Rooms II minimum-room-count utility
- Pluggable **RoomSelectionStrategy**
- Thread-safe concurrent booking

**Deliberately out of scope**: recurring meetings, calendar invites, user auth, persistence/DB,
REST/UI layer, time-zone conversion.

---

## 2. UML

### Class diagram
```mermaid
classDiagram
    class TimeInterval {
      +LocalDateTime start
      +LocalDateTime end
      +overlaps(TimeInterval) bool
    }
    class Attendee { +String email }
    class Booking {
      +String id
      +String title
      +MeetingRoom room
      +TimeInterval interval
      +List~Attendee~ attendees
    }
    class MeetingRoom {
      -List~Booking~ bookings
      +tryBook(Booking) bool  «synchronized»
      +cancel(id) bool  «synchronized»
      +bookingsForDay(day) List~Booking~
    }
    class MeetingScheduler {
      +book(title, interval, attendees) Booking
      +cancel(id)
      +listBookingsForRoomDay(roomId, day) List~Booking~
    }
    class CalendarUtils { +minimumRoomsRequired(meetings) int }
    class RoomSelectionStrategy {
      <<interface>>
      +book(rooms, interval, title, attendees) Optional~Booking~
    }
    class FirstAvailableRoomSelectionStrategy
    class LeastUsedRoomSelectionStrategy

    RoomSelectionStrategy <|.. FirstAvailableRoomSelectionStrategy
    RoomSelectionStrategy <|.. LeastUsedRoomSelectionStrategy
    MeetingScheduler o-- MeetingRoom
    MeetingScheduler --> RoomSelectionStrategy
    MeetingRoom o-- Booking
    Booking --> TimeInterval
    Booking --> Attendee
```

### Book sequence
```mermaid
sequenceDiagram
    participant C as Client
    participant S as MeetingScheduler
    participant P as FirstAvailableStrategy
    participant R as MeetingRoom
    C->>S: book(title, interval, attendees)
    S->>P: book(rooms, interval, title, attendees)
    loop each room until one accepts
        P->>R: tryBook(candidate)  «synchronized»
        R-->>P: true / false
    end
    P-->>S: booking (or empty)
    alt room found
        S->>S: store active booking
        S-->>C: Booking
    else no room free
        S-->>C: throw NoAvailableRoomException
    end
```

---

## 3. Design decisions

| Decision | Reasoning |
| --- | --- |
| **Half-open `TimeInterval`** | `[start,end)` matches calendars: a 10:00 end does not conflict with a 10:00 start. |
| **Room owns its bookings** | Calendar invariants live with the resource they protect. |
| **Strategy for room selection** | First-available and least-used policies can swap without touching the service. |
| **Concurrency at the room, not the scheduler** | Per-room `synchronized tryBook` prevents double-booking while allowing different rooms to proceed in parallel. |
| **`ConcurrentHashMap` for active bookings** | Atomic `remove` ensures cancellation consumes a booking exactly once. |
| **Sweep-line utility** | `minimumRoomsRequired` is independent of scheduler state and easy to test. |

### Concurrency model (the key part)
`MeetingRoom.tryBook` is `synchronized`, so the overlap scan and calendar insert are a single atomic
step. The selection strategy simply iterates rooms and calls `tryBook`; when 50 threads race for the
same slot across 5 rooms, exactly 5 win and no room id is claimed twice.

---

## 4. Code flow

```
Main → MeetingScheduler.book
        → RoomSelectionStrategy.book → MeetingRoom.tryBook (atomic overlap check + insert)
        → store Booking in ConcurrentHashMap
MeetingScheduler.cancel → remove booking (atomic) → MeetingRoom.cancel
CalendarUtils.minimumRoomsRequired → sort starts/ends → sweep active count
```

Package layout:
```
com.example.scheduler
├── model/      Attendee, TimeInterval, MeetingRoom, Booking
├── strategy/   RoomSelectionStrategy + FirstAvailable + LeastUsed
├── service/    MeetingScheduler, CalendarUtils
├── exception/  NoAvailableRoomException, BookingNotFoundException
└── Main.java   runnable demo
```

---

## 5. How to run

Prerequisites: JDK 17+ and Maven.

```powershell
cd java

# run the test suite (5 tests incl. the concurrency race test)
mvn test

# run the demo
mvn -q compile exec:java "-Dexec.mainClass=com.example.scheduler.Main"
```

Expected demo output:
```
Rooms at open: 3
Booked Planning in Room-A [2024-01-01T09:00, 2024-01-01T10:00)
Booked Standup  in Room-B [2024-01-01T09:30, 2024-01-01T10:00)
Bookings in Room-A on 2024-01-01: 1
Minimum rooms needed for sample: 2
Cancelled Planning; Room-A bookings now: 0
```

---

## 6. Tests

`MeetingSchedulerTest` covers:
- booking success allocates the first free room
- overlapping interval rejected when no room is free
- cancel frees the room for the same interval
- Meeting Rooms II utility correctness for half-open intervals
- **concurrency**: 50 threads race for 5 rooms → exactly 5 succeed, 0 duplicate room ids

---

## 7. Extending (what a follow-up would add)
- **Recurring meetings**: expand into intervals and reuse the same overlap checks.
- **Invites/notifications**: publish an event after `book`/`cancel`.
- **Capacity-aware selection**: filter rooms by attendee count before applying strategy.
- **Persistence**: replace the in-memory maps/lists with repository interfaces.
