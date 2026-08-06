"""Deterministic Job Scheduler tests, including the required concurrency submission race."""

from __future__ import annotations

import threading
from datetime import datetime, timedelta, timezone

from scheduler.models import Job
from scheduler.scheduler import JobScheduler


class MutableClock:
    """Hand-advanced clock so tests never sleep."""

    def __init__(self, start: datetime) -> None:
        self._now = start

    def __call__(self) -> datetime:
        return self._now

    def advance(self, delta: timedelta) -> None:
        self._now += delta


START = datetime(2024, 1, 1, 10, 0, tzinfo=timezone.utc)


def test_one_shot_runs_only_when_tick_reaches_run_at():
    clock = MutableClock(START)
    scheduler = JobScheduler(clock)
    runs = 0

    def action() -> None:
        nonlocal runs
        runs += 1

    scheduler.schedule(Job("report", action), START + timedelta(minutes=10))

    assert scheduler.tick(START + timedelta(minutes=9)) == 0
    assert runs == 0
    clock.advance(timedelta(minutes=10))
    assert scheduler.tick(clock()) == 1
    assert runs == 1
    assert scheduler.run_count("report") == 1


def test_jobs_run_in_due_time_order_within_single_tick():
    scheduler = JobScheduler(clock=lambda: START)
    order: list[str] = []

    scheduler.schedule(Job("second", lambda: order.append("second")), START + timedelta(seconds=20))
    scheduler.schedule(Job("first", lambda: order.append("first")), START + timedelta(seconds=10))

    assert scheduler.tick(START + timedelta(seconds=30)) == 2
    assert order == ["first", "second"]


def test_recurring_job_reschedules_after_each_due_tick():
    clock = MutableClock(START)
    scheduler = JobScheduler(clock)
    scheduler.schedule_recurring(Job("heartbeat", lambda: None), timedelta(minutes=5))

    clock.advance(timedelta(minutes=5))
    assert scheduler.tick(clock()) == 1
    clock.advance(timedelta(minutes=5))
    assert scheduler.tick(clock()) == 1
    clock.advance(timedelta(minutes=5))
    assert scheduler.tick(clock()) == 1

    assert scheduler.run_count("heartbeat") == 3
    assert scheduler.last_run_at("heartbeat") == clock()


def test_cancelled_job_does_not_run():
    scheduler = JobScheduler(clock=lambda: START)
    runs = 0

    def action() -> None:
        nonlocal runs
        runs += 1

    scheduler.schedule(Job("obsolete", action), START + timedelta(seconds=1))
    scheduler.cancel("obsolete")

    assert scheduler.tick(START + timedelta(seconds=10)) == 0
    assert runs == 0
    assert scheduler.run_count("obsolete") == 0


def test_concurrent_submissions_are_all_enqueued_and_run_once():
    thread_count = 50
    scheduler = JobScheduler(clock=lambda: START)
    gate = threading.Event()
    run_counter = 0
    run_counter_lock = threading.Lock()

    def make_action():
        def action() -> None:
            nonlocal run_counter
            with run_counter_lock:
                run_counter += 1
        return action

    def producer(i: int) -> None:
        gate.wait()  # release all producers together to maximize heap contention
        scheduler.schedule(Job(f"job-{i}", make_action()), START + timedelta(seconds=1))

    workers = [threading.Thread(target=producer, args=(i,)) for i in range(thread_count)]
    for worker in workers:
        worker.start()
    gate.set()
    for worker in workers:
        worker.join(timeout=10)

    assert scheduler.pending_count() == thread_count
    assert scheduler.tick(START + timedelta(seconds=60)) == thread_count
    assert run_counter == thread_count
