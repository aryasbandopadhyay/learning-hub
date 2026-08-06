"""Runnable deterministic demo.

Run:  python -m scheduler.main   (from the python/ directory)
"""

from __future__ import annotations

from datetime import datetime, timedelta, timezone

from .models import Job
from .scheduler import JobScheduler


def main() -> None:
    start = datetime(2024, 1, 1, 10, 0, tzinfo=timezone.utc)
    scheduler = JobScheduler(clock=lambda: start)
    audit_log: list[str] = []

    email = Job("email-digest", lambda: audit_log.append("email-digest"))
    cleanup = Job("cache-cleanup", lambda: audit_log.append("cache-cleanup"))
    heartbeat = Job("heartbeat", lambda: audit_log.append("heartbeat"))

    scheduler.schedule_after(email, timedelta(minutes=5))
    scheduler.schedule(cleanup, start + timedelta(minutes=2))
    scheduler.schedule_recurring(heartbeat, timedelta(minutes=3))

    print("Pending jobs at start:", scheduler.pending_count())
    print("Tick +2m ran:", scheduler.tick(start + timedelta(minutes=2)))
    print("Tick +3m ran:", scheduler.tick(start + timedelta(minutes=3)))
    print("Tick +5m ran:", scheduler.tick(start + timedelta(minutes=5)))
    print("Run order:", audit_log)
    print("Heartbeat runs:", scheduler.run_count("heartbeat"))


if __name__ == "__main__":
    main()
