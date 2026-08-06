"""Domain models for users, channel choices, send results, and observer events."""

from __future__ import annotations

from dataclasses import dataclass
from datetime import datetime
from enum import Enum
from typing import FrozenSet


class ChannelType(Enum):
    EMAIL = "EMAIL"
    SMS = "SMS"
    PUSH = "PUSH"


class DeliveryStatus(Enum):
    SENT = "SENT"
    FAILED = "FAILED"


@dataclass(frozen=True)
class User:
    """Recipient profile plus preferences used when notify() gets no explicit channels."""

    id: str
    name: str
    email: str
    phone_number: str
    device_token: str
    preferred_channels: FrozenSet[ChannelType]

    def recipient_for(self, channel_type: ChannelType) -> str:
        return {
            ChannelType.EMAIL: self.email,
            ChannelType.SMS: self.phone_number,
            ChannelType.PUSH: self.device_token,
        }[channel_type]


@dataclass(frozen=True)
class SentMessage:
    """Immutable fake-provider record captured in memory instead of sent over a network."""

    channel_type: ChannelType
    recipient: str
    message: str
    sent_at: datetime


@dataclass(frozen=True)
class NotificationResult:
    """Per-channel result returned to the caller; failures are values, not thrown exceptions."""

    channel_type: ChannelType
    status: DeliveryStatus
    attempts: int
    error_message: str | None = None


@dataclass(frozen=True)
class NotificationEvent:
    """Event emitted to observers after a channel reaches final SENT/FAILED status."""

    user: User
    channel_type: ChannelType
    status: DeliveryStatus
    message: str
    attempts: int
    error_message: str | None
    occurred_at: datetime
