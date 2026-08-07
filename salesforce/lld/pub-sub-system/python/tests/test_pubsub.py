"""End-to-end tests for the Pub-Sub MVP, including concurrent publishers."""

from __future__ import annotations

import threading
from datetime import datetime, timezone

from pubsub import PubSubBroker


def make_broker() -> PubSubBroker:
    return PubSubBroker(clock=lambda: datetime(2024, 1, 1, tzinfo=timezone.utc))


def test_publish_delivers_to_multiple_subscribers():
    broker = make_broker()
    try:
        delivered = threading.Event()
        remaining = 2
        guard = threading.Lock()
        received_a: list[str] = []
        received_b: list[str] = []

        def mark() -> None:
            nonlocal remaining
            with guard:
                remaining -= 1
                if remaining == 0:
                    delivered.set()

        broker.subscribe("orders", "a", lambda m: (received_a.append(m.payload), mark()))
        broker.subscribe("orders", "b", lambda m: (received_b.append(m.payload), mark()))

        broker.publish("orders", "created")

        assert delivered.wait(timeout=5)
        assert received_a == ["created"]
        assert received_b == ["created"]
    finally:
        broker.shutdown()


def test_unsubscribe_stops_future_delivery():
    broker = make_broker()
    try:
        first_delivery = threading.Event()
        unexpected_second_delivery = threading.Event()
        received: list[str] = []

        def subscriber(message):
            received.append(message.payload)
            if message.payload == "before-unsubscribe":
                first_delivery.set()
            else:
                unexpected_second_delivery.set()

        broker.subscribe("orders", "email", subscriber)
        broker.publish("orders", "before-unsubscribe")
        assert first_delivery.wait(timeout=5)
        broker.unsubscribe("orders", "email")
        broker.publish("orders", "after-unsubscribe")

        assert not unexpected_second_delivery.wait(timeout=0.3)
        assert received == ["before-unsubscribe"]
    finally:
        broker.shutdown()


def test_each_subscriber_receives_each_message_exactly_once():
    broker = make_broker()
    try:
        messages = 25
        delivered = threading.Event()
        offsets: set[int] = set()
        guard = threading.Lock()

        def subscriber(message):
            with guard:
                offsets.add(message.offset)
                if len(offsets) == messages:
                    delivered.set()

        broker.subscribe("orders", "email", subscriber)
        for i in range(messages):
            broker.publish("orders", f"event-{i}")

        assert delivered.wait(timeout=5)
        assert offsets == set(range(messages))
        assert broker.next_offset("orders", "email") == messages
    finally:
        broker.shutdown()


def test_concurrent_publishers_deliver_all_messages_to_all_subscribers():
    broker = make_broker()
    try:
        publishers = 8
        per_publisher = 25
        total = publishers * per_publisher
        delivered = threading.Event()
        remaining = total * 2
        guard = threading.Lock()
        email_payloads: set[str] = set()
        analytics_payloads: set[str] = set()
        offsets: set[int] = set()

        def mark() -> None:
            nonlocal remaining
            remaining -= 1
            if remaining == 0:
                delivered.set()

        def email(message):
            with guard:
                email_payloads.add(message.payload)
                offsets.add(message.offset)
                mark()

        def analytics(message):
            with guard:
                analytics_payloads.add(message.payload)
                mark()

        broker.subscribe("orders", "email", email)
        broker.subscribe("orders", "analytics", analytics)

        start = threading.Event()

        def publisher(publisher_id: int) -> None:
            start.wait()
            for i in range(per_publisher):
                broker.publish("orders", f"p{publisher_id}-m{i}")

        workers = [threading.Thread(target=publisher, args=(p,)) for p in range(publishers)]
        for worker in workers:
            worker.start()
        start.set()
        for worker in workers:
            worker.join(timeout=10)

        assert delivered.wait(timeout=10)
        assert len(email_payloads) == total
        assert len(analytics_payloads) == total
        assert email_payloads == analytics_payloads
        assert len(offsets) == total
    finally:
        broker.shutdown()
