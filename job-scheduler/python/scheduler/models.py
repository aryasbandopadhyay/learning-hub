"""Domain models: Job as a Command and ScheduledTask as a priority-queue entry."""

from __future__ import annotations

from dataclasses import dataclass, field
from datetime import datetime, timedelta
from typing import Callable


@dataclass(frozen=True)
class Job:
    """Command object: stable id + callable action.

    The scheduler only calls ``job.run()``. It does not know or care whether the command sends an
    email, refreshes a cache, or publishes a metric, which is the Command pattern in a tiny form.
    """

    id: str
    action: Callable[[], None]

    def run(self) -> None:
        self.action()


@dataclass(frozen=True)
class ScheduledTask:
    """One min-heap entry.

    ``sort_index`` is a tuple because Python's ``heapq`` compares tuple fields left to right:
    next run time first, then insertion sequence for FIFO ties, then job id as a stable fallback.
    ``interval`` is ``None`` for one-shot jobs and a ``timedelta`` for recurring jobs.
    """

    next_run_at: datetime
    sequence: int
    job: Job = field(compare=False)
    interval: timedelta | None = field(default=None, compare=False)

    @property
    def sort_index(self) -> tuple[datetime, int, str]:
        return (self.next_run_at, self.sequence, self.job.id)

    @property
    def is_recurring(self) -> bool:
        return self.interval is not None
