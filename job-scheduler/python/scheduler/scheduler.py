"""Thread-safe, deterministic in-memory Job Scheduler."""

from __future__ import annotations

import heapq
import itertools
import threading
from datetime import datetime, timedelta, timezone
from typing import Callable

from .exceptions import InvalidScheduleError
from .models import Job, ScheduledTask


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class JobScheduler:
    """Min-heap scheduler with an injected clock and explicit ``tick``.

    The priority queue keeps the earliest next-run job at index 0. ``tick(now)`` repeatedly pops due
    tasks, runs them, and re-enqueues recurring tasks at ``now + interval``. Tests therefore advance
    a fake clock and call tick directly instead of sleeping or waiting for a background thread.

    ``heapq`` is not thread-safe, so ``_lock`` guards every heap mutation/read. User jobs run after
    the lock is released, which means concurrent producers can still submit work while a tick drains
    already-due jobs.
    """

    def __init__(self, clock: Callable[[], datetime] = _utc_now) -> None:
        self._clock = clock
        self._heap: list[tuple[datetime, int, str, ScheduledTask]] = []
        self._sequence = itertools.count()
        self._lock = threading.RLock()
        self._cancelled: set[str] = set()
        self._run_counts: dict[str, int] = {}
        self._last_run_at: dict[str, datetime] = {}

    def schedule(self, job: Job, run_at: datetime) -> ScheduledTask:
        """Schedule a one-shot job at an absolute datetime."""
        task = ScheduledTask(run_at, next(self._sequence), job)
        with self._lock:
            self._cancelled.discard(job.id)
            self._run_counts.setdefault(job.id, 0)
            heapq.heappush(self._heap, (*task.sort_index, task))
        return task

    def schedule_after(self, job: Job, delay: timedelta) -> ScheduledTask:
        """Schedule a one-shot job relative to the injected clock."""
        if delay < timedelta(0):
            raise InvalidScheduleError("Delay cannot be negative")
        return self.schedule(job, self._clock() + delay)

    def schedule_recurring(self, job: Job, interval: timedelta) -> ScheduledTask:
        """Schedule a recurring job; its first run is one interval from clock now."""
        if interval <= timedelta(0):
            raise InvalidScheduleError("Recurring interval must be positive")
        task = ScheduledTask(self._clock() + interval, next(self._sequence), job, interval)
        with self._lock:
            self._cancelled.discard(job.id)
            self._run_counts.setdefault(job.id, 0)
            heapq.heappush(self._heap, (*task.sort_index, task))
        return task

    def cancel(self, job_id: str) -> bool:
        """Cancel by id using lazy deletion.

        Removing arbitrary items from a heap is O(n). Marking the id cancelled is O(1), and stale
        heap entries are skipped when they become due, which is simpler and interview-friendly.
        """
        with self._lock:
            before = job_id in self._cancelled
            self._cancelled.add(job_id)
            return not before

    def tick(self, now: datetime) -> int:
        """Run all jobs whose next_run_at <= now. Returns the number of executions."""
        executed = 0
        while True:
            task = self._poll_due(now)
            if task is None:
                return executed
            if self._is_cancelled(task.job.id):
                continue

            task.job.run()
            with self._lock:
                self._run_counts[task.job.id] = self._run_counts.get(task.job.id, 0) + 1
                self._last_run_at[task.job.id] = now
            executed += 1

            if task.interval is not None and not self._is_cancelled(task.job.id):
                next_task = ScheduledTask(now + task.interval, next(self._sequence), task.job, task.interval)
                with self._lock:
                    heapq.heappush(self._heap, (*next_task.sort_index, next_task))

    def run_count(self, job_id: str) -> int:
        with self._lock:
            return self._run_counts.get(job_id, 0)

    def last_run_at(self, job_id: str) -> datetime | None:
        with self._lock:
            return self._last_run_at.get(job_id)

    def pending_count(self) -> int:
        with self._lock:
            return sum(1 for _, _, job_id, _ in self._heap if job_id not in self._cancelled)

    def _poll_due(self, now: datetime) -> ScheduledTask | None:
        with self._lock:
            while self._heap:
                run_at, _, job_id, task = self._heap[0]
                if run_at > now:
                    return None
                heapq.heappop(self._heap)
                if job_id not in self._cancelled:
                    return task
            return None

    def _is_cancelled(self, job_id: str) -> bool:
        with self._lock:
            return job_id in self._cancelled
