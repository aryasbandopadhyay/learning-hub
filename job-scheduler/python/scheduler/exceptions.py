"""Scheduler-specific exceptions."""


class SchedulerError(Exception):
    """Base exception for invalid scheduler operations."""


class InvalidScheduleError(SchedulerError):
    """Raised for impossible schedules, such as a non-positive recurring interval."""
