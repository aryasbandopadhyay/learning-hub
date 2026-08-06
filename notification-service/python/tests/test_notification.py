"""End-to-end tests for Notification Service, including retry and concurrency."""

from __future__ import annotations

import threading
from datetime import datetime, timezone

from notification.channels import ChannelFactory, EmailChannel, InMemoryChannel, NotificationChannel
from notification.exceptions import NotificationDeliveryError
from notification.models import ChannelType, DeliveryStatus, User
from notification.service import AuditListener, NotificationService


def fixed_clock() -> datetime:
    return datetime(2024, 1, 1, 10, 0, tzinfo=timezone.utc)


def make_user(preferences: frozenset[ChannelType]) -> User:
    return User(
        id="u1",
        name="Asha",
        email="asha@example.com",
        phone_number="+919999999999",
        device_token="device-123",
        preferred_channels=preferences,
    )


def test_notify_routes_message_to_requested_channels():
    factory = ChannelFactory(clock=fixed_clock)
    service = NotificationService(factory, max_attempts=3, clock=fixed_clock)

    results = service.notify(
        make_user(frozenset({ChannelType.EMAIL})),
        "Hello",
        [ChannelType.EMAIL, ChannelType.SMS],
    )

    email = factory.create(ChannelType.EMAIL)
    assert isinstance(email, EmailChannel)
    assert results[ChannelType.EMAIL].status is DeliveryStatus.SENT
    assert results[ChannelType.SMS].status is DeliveryStatus.SENT
    assert len(email.sent_messages) == 1
    assert email.sent_messages[0].recipient == "asha@example.com"
    assert email.sent_messages[0].message == "Hello"


def test_notify_without_channels_uses_user_preferences():
    factory = ChannelFactory(clock=fixed_clock)
    service = NotificationService(factory, max_attempts=3, clock=fixed_clock)

    results = service.notify(
        make_user(frozenset({ChannelType.EMAIL, ChannelType.PUSH})),
        "Preference based",
    )

    assert set(results) == {ChannelType.EMAIL, ChannelType.PUSH}
    assert len(factory.create(ChannelType.EMAIL).sent_messages) == 1  # type: ignore[attr-defined]
    assert len(factory.create(ChannelType.SMS).sent_messages) == 0  # type: ignore[attr-defined]
    assert len(factory.create(ChannelType.PUSH).sent_messages) == 1  # type: ignore[attr-defined]


def test_retry_eventually_succeeds_before_max_attempts():
    flaky = FailingThenSuccessChannel(ChannelType.EMAIL, failures=2)
    service = NotificationService(
        ChannelFactory({ChannelType.EMAIL: flaky}),
        max_attempts=3,
        clock=fixed_clock,
    )

    result = service.notify(make_user(frozenset({ChannelType.EMAIL})), "Retry me")[ChannelType.EMAIL]

    assert result.status is DeliveryStatus.SENT
    assert result.attempts == 3
    assert len(flaky.sent_messages) == 1


def test_retry_failure_is_recorded_and_not_thrown():
    failing = AlwaysFailingChannel(ChannelType.SMS)
    service = NotificationService(
        ChannelFactory({ChannelType.SMS: failing}),
        max_attempts=3,
        clock=fixed_clock,
    )

    result = service.notify(make_user(frozenset({ChannelType.SMS})), "Will fail")[ChannelType.SMS]

    assert result.status is DeliveryStatus.FAILED
    assert result.attempts == 3
    assert result.error_message == "downstream unavailable"
    assert failing.attempts == 3


def test_observer_receives_sent_and_failed_events():
    failing = AlwaysFailingChannel(ChannelType.SMS)
    factory = ChannelFactory(clock=fixed_clock)
    factory.register(failing)
    service = NotificationService(factory, max_attempts=2, clock=fixed_clock)
    audit = AuditListener()
    service.register_listener(audit)

    service.notify(make_user(frozenset({ChannelType.EMAIL, ChannelType.SMS})), "Observe")

    assert len(audit.events) == 2
    assert {event.status for event in audit.events} == {DeliveryStatus.SENT, DeliveryStatus.FAILED}


def test_concurrent_notifications_are_recorded_exactly_once():
    threads = 50
    factory = ChannelFactory(clock=fixed_clock)
    service = NotificationService(factory, max_attempts=3, clock=fixed_clock)
    audit = AuditListener()
    service.register_listener(audit)
    start = threading.Event()

    def worker(i: int) -> None:
        start.wait()
        service.notify(make_user(frozenset({ChannelType.EMAIL})), f"Message {i}")

    workers = [threading.Thread(target=worker, args=(i,)) for i in range(threads)]
    for worker_thread in workers:
        worker_thread.start()
    start.set()
    for worker_thread in workers:
        worker_thread.join(timeout=10)

    email = factory.create(ChannelType.EMAIL)
    assert isinstance(email, EmailChannel)
    assert len(email.sent_messages) == threads
    assert len({message.message for message in email.sent_messages}) == threads
    assert len(audit.events) == threads


class FailingThenSuccessChannel(InMemoryChannel):
    def __init__(self, channel_type: ChannelType, failures: int) -> None:
        super().__init__(channel_type, fixed_clock)
        self._remaining_failures = failures
        self._lock_for_failures = threading.Lock()

    def send(self, recipient: str, message: str) -> None:
        with self._lock_for_failures:
            if self._remaining_failures > 0:
                self._remaining_failures -= 1
                raise NotificationDeliveryError("temporary failure")
        super().send(recipient, message)


class AlwaysFailingChannel(NotificationChannel):
    def __init__(self, channel_type: ChannelType) -> None:
        self._type = channel_type
        self.attempts = 0
        self._lock = threading.Lock()

    @property
    def type(self) -> ChannelType:
        return self._type

    def send(self, recipient: str, message: str) -> None:
        with self._lock:
            self.attempts += 1
        raise NotificationDeliveryError("downstream unavailable")
