# Elevator System — LLD Machine Coding (Python)

An end-to-end MVP of a multi-car elevator system, built for an SDE2 machine-coding round. It
demonstrates OOP modelling, the **State** and **Strategy** patterns, deterministic simulation, and
thread-safe request intake.

> A parallel Java implementation lives in `../java` with its own README. Both produce identical
> demo output. The class structure is intentionally 1:1 between the two languages.

---

## 1. Why this MVP?

A machine-coding interviewer looks for clean OOP, useful patterns, concurrency awareness, and green
tests — delivered quickly. The MVP is the **smallest elevator system that still exercises all of
those**:

**In scope**
- Building with configurable floors and one or more elevator cars
- External request: `(floor, direction)`
- Internal request: `(car, target_floor)`
- `ElevatorController.step()` advances every car one deterministic tick
- **State** pattern via `ElevatorState`
- **Strategy** pattern via `SchedulingStrategy`
- Thread-safe request intake with `threading.Lock` + `deque`

**Deliberately out of scope** (extension points): weight limits, express/zoning policies, real-time
threading, door timing, passenger capacity, persistence, REST/UI.

Why step-driven instead of real movement threads? Tests should never depend on sleeps or timing. A
single `step()` is a deterministic clock tick: either move one floor, or open/close doors.

---

## 2. UML

### Class diagram
```mermaid
classDiagram
    class ElevatorController {
      -deque external_requests
      -deque internal_requests
      -Lock request_lock
      +submit_external_request(floor, direction)
      +submit_internal_request(car_id, target_floor)
      +step()
    }
    class Elevator {
      +int id
      +int current_floor
      +ElevatorState state
      -set target_floors
      -Lock lock
      +add_target_floor(floor)
      +step()
    }
    class ElevatorState {
      <<Enum>>
      IDLE
      MOVING_UP
      MOVING_DOWN
      DOORS_OPEN
    }
    class ExternalRequest
    class InternalRequest
    class SchedulingStrategy {
      <<abstract>>
      +select_car(elevators, request)
    }
    class NearestCarSchedulingStrategy

    SchedulingStrategy <|-- NearestCarSchedulingStrategy
    ElevatorController o-- Elevator
    ElevatorController --> SchedulingStrategy
    ElevatorController --> ExternalRequest
    ElevatorController --> InternalRequest
    Elevator --> ElevatorState
```

### State diagram
```mermaid
stateDiagram-v2
    [*] --> IDLE
    IDLE --> MOVING_UP: target above
    IDLE --> MOVING_DOWN: target below
    IDLE --> DOORS_OPEN: target at current floor
    MOVING_UP --> DOORS_OPEN: reaches target
    MOVING_DOWN --> DOORS_OPEN: reaches target
    DOORS_OPEN --> MOVING_UP: higher target remains
    DOORS_OPEN --> MOVING_DOWN: lower target remains
    DOORS_OPEN --> IDLE: no targets
    MOVING_UP --> MOVING_DOWN: no higher targets
    MOVING_DOWN --> MOVING_UP: no lower targets
```

---

## 3. Design decisions

| Decision | Reasoning |
| --- | --- |
| **Step-driven simulation** | Deterministic tests; no sleeps, timers, or background movement threads. |
| **`ElevatorState` enum** | Compact State pattern suitable for an interview MVP. |
| **Target floors as a set** | Deduplicates repeated button presses; sorted snapshots are exposed for tests/debugging. |
| **Strategy for scheduling** | Nearest-car policy can be swapped without changing controller code. |
| **Lock + deque for intake** | Multiple threads can submit requests safely; simulation drains them in order. |
| **Single-threaded `step()`** | Keeps movement deterministic while still proving thread-safe request submission. |

### Concurrency model (the key part)
Request intake is concurrent; movement is not. Producers call `submit_external_request` or
`submit_internal_request`, which append under `threading.Lock`. `step()` drains those deques and
then advances each car. The concurrency test starts 50 threads and asserts all 50 hall calls are in
the queue, proving none were lost.

---

## 4. Code flow

```
client → ElevatorController.submit_external_request → locked deque
step → drain queues → SchedulingStrategy.select_car → Elevator.add_target_floor
step → Elevator.step → move one floor OR open/close doors
```

Module layout:
```
elevator/
├── models.py       Elevator, state enum, direction enum, request dataclasses
├── strategies.py   SchedulingStrategy + NearestCarSchedulingStrategy
├── controller.py   ElevatorController
├── exceptions.py   InvalidFloorError, ElevatorNotFoundError
└── main.py         runnable deterministic demo
tests/
└── test_elevator.py
```

---

## 5. How to run

Prerequisites: Python 3.10+ and pytest.

```powershell
cd python

# run the suite (4 tests incl. concurrent intake)
python -m pytest -q

# run the demo
python -m elevator.main
```

Expected demo output:
```
Initial: Car 1 at floor 0 (IDLE), Car 2 at floor 6 (IDLE)
Queued requests: 2
Tick 1: Car 1 at floor 1 (MOVING_UP)
Tick 2: Car 1 at floor 2 (DOORS_OPEN)
Tick 3: Car 1 at floor 2 (MOVING_UP)
Tick 4: Car 1 at floor 3 (MOVING_UP)
Tick 5: Car 1 at floor 4 (MOVING_UP)
Tick 6: Car 1 at floor 5 (DOORS_OPEN)
Tick 7: Car 1 at floor 5 (IDLE)
```

---

## 6. Tests

`tests/test_elevator.py` covers:
- single car serves an internal request and opens doors
- direction logic: a car moving up does not reverse while higher floors remain
- nearest-car strategy chooses the closer idle car
- **concurrency**: 50 threads submit external requests; all 50 are queued

---

## 7. Extending
- **Door timing**: model door-open duration as more deterministic ticks.
- **Capacity/weight**: reject internal requests when a car is overloaded.
- **Express/zoning**: replace `SchedulingStrategy`.
- **Real-time system**: put a scheduler loop around `step()`; keep core logic deterministic.
