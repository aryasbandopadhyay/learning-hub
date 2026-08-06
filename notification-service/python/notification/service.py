"""Application service plus Observer support for the Notification Service MVP."""

from __future__ import annotations

import threading
from collections.abc import Callable, Iterable
from datetime import datetime
from typing import Protocol

from .channels import ChannelFactory, utc_now
from .exceptions import NotificationDeliveryError
from .models import ChannelType, DeliveryStatus, NotificationEvent, NotificationResult, User


class NotificationListener(Protocol):
    """Observer protocol: listeners react to final send outcomes."""

    def on_event(self, event: NotificationEvent) -> None:
        ...


class AuditListener:
    """Thread-safe observer that stores events for audit trails and assertions."""

    def __init__(self) -> None:
        self._events: list[NotificationEvent] = []
        self._lock = threading.Lock()

    def on_event(self, event: NotificationEvent) -> None:
        with self._lock:
            self._events.append(event)

    @property
    def events(self) -> tuple[NotificationEvent, ...]:
        with self._lock:
            return tuple(self._events)


class NotificationService:
    """Wires Factory + Strategy + Observer together.

    Concurrency: the service keeps no per-notify mutable state on the instance. Listener registration
    is locked, and each send takes a snapshot so concurrent sends can notify observers safely.
    """

    def __init__(
        self,
        channel_factory: ChannelFactory,
        max_attempts: int,
        clock: Callable[[], datetime] = utc_now,
    ) -> None:
        if max_attempts < 1:
            raise ValueError("max_attempts must be at least 1")
        self._factory = channel_factory
        self._max_attempts = max_attempts
        self._clock = clock
        self._listeners: list[NotificationListener] = []
        self._listeners_lock = threading.Lock()

    def notify(
        self,
        user: User,
        message: str,
        channel_types: Iterable[ChannelType] | None = None,
    ) -> dict[ChannelType, NotificationResult]:
        """Send to explicit channels or user preferences; return final per-channel results."""
        if channel_types is None:
            # Keep demos/tests deterministic even though preferences are stored as a frozenset.
            selected = tuple(channel for channel in ChannelType if channel in user.preferred_channels)
        else:
            selected = tuple(channel_types)
        results: dict[ChannelType, NotificationResult] = {}
        for channel_type in selected:
            result = self._send_with_retry(user, message, channel_type)
            results[channel_type] = result
            self._publish(NotificationEvent(
                user=user,
                channel_type=channel_type,
                status=result.status,
                message=message,
                attempts=result.attempts,
                error_message=result.error_message,
                occurred_at=self._clock(),
            ))
        return results

    def register_listener(self, listener: NotificationListener) -> None:
        with self._listeners_lock:
            self._listeners.append(listener)

    def unregister_listener(self, listener: NotificationListener) -> None:
        with self._listeners_lock:
            self._listeners.remove(listener)

    def _send_with_retry(self, user: User, message: str, channel_type: ChannelType) -> NotificationResult:
        channel = self._factory.create(channel_type)
        recipient = user.recipient_for(channel_type)
        last_error: str | None = None
        for attempt in range(1, self._max_attempts + 1):
            try:
                channel.send(recipient, message)
                return NotificationResult(channel_type, DeliveryStatus.SENT, attempt)
            except (NotificationDeliveryError, RuntimeError) as exc:
                last_error = str(exc)
        return NotificationResult(channel_type, DeliveryStatus.FAILED, self._max_attempts, last_error)

    def _publish(self, event: NotificationEvent) -> None:
        with self._listeners_lock:
            listeners = tuple(self._listeners)
        for listener in listeners:
            listener.on_event(event)
