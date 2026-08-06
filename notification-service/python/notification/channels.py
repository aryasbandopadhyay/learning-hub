"""Channel strategies and factory for the Notification Service MVP."""

from __future__ import annotations

import threading
from abc import ABC, abstractmethod
from collections.abc import Callable, Mapping
from datetime import datetime, timezone

from .models import ChannelType, SentMessage


def utc_now() -> datetime:
    return datetime.now(timezone.utc)


class NotificationChannel(ABC):
    """Strategy pattern: every provider has the same send contract."""

    @property
    @abstractmethod
    def type(self) -> ChannelType:
        """The enum key this strategy supports."""

    @abstractmethod
    def send(self, recipient: str, message: str) -> None:
        """Send or raise NotificationDeliveryError; implementations hide provider details."""


class InMemoryChannel(NotificationChannel):
    """Fake provider that records messages in a thread-safe in-memory sink.

    A Lock guards the list because tests deliberately send from many threads. Returning a tuple gives
    callers a stable snapshot without exposing mutable shared state.
    """

    def __init__(self, channel_type: ChannelType, clock: Callable[[], datetime] = utc_now) -> None:
        self._type = channel_type
        self._clock = clock
        self._sent_messages: list[SentMessage] = []
        self._lock = threading.Lock()

    @property
    def type(self) -> ChannelType:
        return self._type

    def send(self, recipient: str, message: str) -> None:
        with self._lock:
            self._sent_messages.append(SentMessage(self.type, recipient, message, self._clock()))

    @property
    def sent_messages(self) -> tuple[SentMessage, ...]:
        with self._lock:
            return tuple(self._sent_messages)


class EmailChannel(InMemoryChannel):
    def __init__(self, clock: Callable[[], datetime] = utc_now) -> None:
        super().__init__(ChannelType.EMAIL, clock)


class SmsChannel(InMemoryChannel):
    def __init__(self, clock: Callable[[], datetime] = utc_now) -> None:
        super().__init__(ChannelType.SMS, clock)


class PushChannel(InMemoryChannel):
    def __init__(self, clock: Callable[[], datetime] = utc_now) -> None:
        super().__init__(ChannelType.PUSH, clock)


class ChannelFactory:
    """Factory pattern: resolves ChannelType to the configured channel strategy."""

    def __init__(
        self,
        channels: Mapping[ChannelType, NotificationChannel] | None = None,
        clock: Callable[[], datetime] = utc_now,
    ) -> None:
        self._channels: dict[ChannelType, NotificationChannel] = dict(channels or {
            ChannelType.EMAIL: EmailChannel(clock),
            ChannelType.SMS: SmsChannel(clock),
            ChannelType.PUSH: PushChannel(clock),
        })
        self._lock = threading.Lock()

    def create(self, channel_type: ChannelType) -> NotificationChannel:
        with self._lock:
            try:
                return self._channels[channel_type]
            except KeyError as exc:  # pragma: no cover - defensive
                raise ValueError(f"Unsupported channel type: {channel_type}") from exc

    def register(self, channel: NotificationChannel) -> None:
        with self._lock:
            self._channels[channel.type] = channel

    def snapshot(self) -> dict[ChannelType, NotificationChannel]:
        with self._lock:
            return dict(self._channels)
