# Job Scheduler — LLD Machine Coding (Java)

An end-to-end MVP of an in-memory job scheduler, built for an SDE2 machine-coding round. It
showcases a **priority queue**, the **Command** pattern, deterministic clock-driven tests, and
thread-safe concurrent job submission.

> A parallel Python implementation lives in `../python` with its own README. Both produce identical
> demo output.

---

## 1. Why this MVP?

A machine-coding interviewer looks for clean OOP, a useful pattern, correct concurrency, and working
tests. This MVP is the **smallest scheduler that still exercises all of those**:

**In scope**
- `schedule(job, runAt)` for one-shot absolute scheduling
- `scheduleAfter(job, delay)` for one-shot relative scheduling
- `scheduleRecurring(job, interval)` for fixed-interval recurring jobs
- Min-heap / priority queue ordered by next-run time, FIFO for ties
- `tick(now)` deterministic engine driven by an injected `Clock`
- Run counts and last-run timestamps for assertions
- `cancel(jobId)`
- Thread-safe concurrent submissions

**Why tick/clock-driven?** Real scheduler threads need sleeps, polling intervals, and timing
margins; those make unit tests slow and flaky. The core engine here is deterministic: tests advance
a mutable clock and call `tick(now)`. A production/demo worker can be a thin optional layer that
periodically calls `tick(clock.instant())`, while the tested logic remains pure and stable.

**Deliberately out of scope**: cron expressions, distributed scheduling/leader election,
persistence/recovery, misfire policies, and thread-pool sizing/tuning. See *Extending* below.

---

## 2. UML

### Class diagram
```mermaid
classDiagram
    class Job {
      +String id
      +run()
    }
    class ScheduledTask {
      +Job job
      +Instant nextRunAt
      +Optional~Duration~ recurringInterval
      +compareTo(other)
    }
    class JobScheduler {
      -PriorityQueue~ScheduledTask~ queue
      -ReentrantLock queueLock
      -Clock clock
      +schedule(job, runAt)
      +scheduleAfter(job, delay)
      +scheduleRecurring(job, interval)
      +tick(now) int
      +cancel(jobId) bool
      +getRunCount(jobId) int
      +getLastRunAt(jobId) Optional~Instant~
    }
    class SchedulerException
    class InvalidScheduleException

    SchedulerException <|-- InvalidScheduleException
    JobScheduler o-- ScheduledTask
    ScheduledTask --> Job
```

### `tick()` sequence
```mermaid
sequenceDiagram
    participant C as Test / Worker
    participant S as JobScheduler
    participant Q as PriorityQueue
    participant J as Job
    C->>S: tick(now)
    loop while queue top nextRunAt <= now
        S->>Q: poll due task «under lock»
        S->>J: run() «outside lock»
        S->>S: increment run count, set lastRunAt
        alt recurring and not cancelled
            S->>Q: enqueue at now + interval «under lock»
        end
    end
    S-->>C: executions count
```

---

## 3. Design decisions

| Decision | Reasoning |
| --- | --- |
| **PriorityQueue min-heap** | Earliest job is always at the top; schedule/poll are O(log n). |
| **`Job` as Command** | Scheduler is decoupled from business work; it just invokes `run()`. |
| **Injected `Clock` + `tick(now)`** | Deterministic tests without `Thread.sleep` or real worker timing. |
| **Tie-break by insertion sequence** | Equal run times execute in submission order, giving stable behavior. |
| **Lazy cancellation set** | `cancel` is O(1); stale heap entries are skipped when due. |
| **Lock around heap only** | `PriorityQueue` is not thread-safe; the lock prevents lost submissions while jobs run outside the lock. |
| **Optional Strategy extension** | A future `NextRunPolicy` could compute next runs (fixed rate, backoff, cron) without changing the heap engine. |

### Concurrency model (the key part)
`PriorityQueue` cannot be safely mutated by multiple threads, so `queueLock` guards every enqueue,
peek, and poll. `tick()` releases that lock before running user code, so a slow job does not block
other threads from submitting new jobs. The test `concurrentSubmissionsAreAllEnqueuedAndRunOnce`
starts 50 producer threads at once, then ticks far into the future and asserts all 50 jobs ran
exactly once.

---

## 4. Code flow

```
Main/Test → JobScheduler.schedule* → enqueue ScheduledTask in PriorityQueue
tick(now) → poll due tasks in heap order → Job.run → update stats
          → if recurring, enqueue a new ScheduledTask at now + interval
cancel(id) → mark id cancelled → tick skips matching heap entries
```

Package layout:
```
com.example.scheduler
├── model/       Job, ScheduledTask
├── scheduler/   JobScheduler
├── exception/   SchedulerException, InvalidScheduleException
└── Main.java    deterministic runnable demo
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
Pending jobs at start: 3
Tick +2m ran: 1
Tick +3m ran: 1
Tick +5m ran: 1
Run order: [cache-cleanup, heartbeat, email-digest]
Heartbeat runs: 1
```

---

## 6. Tests

`JobSchedulerTest` covers:
- one-shot job runs only when `tick(now)` reaches its due time
- ordering across two jobs due at different times in one tick
- recurring job reschedules and runs across 3 deterministic interval ticks
- cancellation prevents execution
- **concurrency**: 50 threads submit jobs concurrently → all 50 are enqueued and each runs once

---

## 7. Extending (what a follow-up would add)
- **Cron expressions**: add a `NextRunPolicy` strategy that computes the next matching instant.
- **Distributed scheduling**: leader election + sharded ownership of job ids.
- **Persistence**: repository abstraction to reload heap state after process restart.
- **Misfire policies**: choose catch-up, skip, or coalesce when the scheduler was down.
- **Thread-pool tuning**: execute due jobs through an executor with bounded queue/backpressure.
