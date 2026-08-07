"""Meeting Room Scheduler LLD package."""

from .models import Attendee, Booking, MeetingRoom, TimeInterval
from .scheduler import MeetingScheduler, minimum_rooms_required
from .strategies import FirstAvailableRoomSelectionStrategy, LeastUsedRoomSelectionStrategy

__all__ = [
    "Attendee",
    "Booking",
    "MeetingRoom",
    "TimeInterval",
    "MeetingScheduler",
    "minimum_rooms_required",
    "FirstAvailableRoomSelectionStrategy",
    "LeastUsedRoomSelectionStrategy",
]
