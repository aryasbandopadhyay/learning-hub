"""Runnable demo: create a topic, subscribe observers, publish events.

Run:  python -m pubsub.main   (from the python/ directory)
"""

from __future__ import annotations

from datetime import datetime, timezone
from threading import Event, Lock

from .broker import PubSubBroker
from .models import Message


def _format(subscriber_name: str, message: Message) -> str:
    return f"{subscriber_name} received {message.topic_name}#{message.offset} -> {message.payload}"


def main() -> None:
    broker = PubSubBroker(clock=lambda: datetime(2024, 1, 1, tzinfo=timezone.utc))
    try:
        broker.create_topic("orders")
        print("Created topic orders")

        email_lines: list[str] = []
        analytics_lines: list[str] = []
        first_count = 0
        second_count = 0
        first_done = Event()
        second_done = Event()
        count_lock = Lock()

        def mark_delivered(message: Message) -> None:
            nonlocal first_count, second_count
            with count_lock:
                if message.offset == 0:
                    first_count += 1
                    if first_count == 2:
                        first_done.set()
                elif message.offset == 1:
                    second_count += 1
                    if second_count == 2:
                        second_done.set()

        def email(message: Message) -> None:
            email_lines.append(_format("email-service", message))
            mark_delivered(message)

        def analytics(message: Message) -> None:
            analytics_lines.append(_format("analytics-service", message))
            mark_delivered(message)

        broker.subscribe("orders", "email-service", email)
        print("Subscribed email-service to orders")
        broker.subscribe("orders", "analytics-service", analytics)
        print("Subscribed analytics-service to orders")

        m0 = broker.publish("orders", "order-1-created")
        print(f"Published orders#{m0.offset}: {m0.payload}")
        first_done.wait(timeout=5)
        print(email_lines[0])
        print(analytics_lines[0])

        m1 = broker.publish("orders", "order-2-paid")
        print(f"Published orders#{m1.offset}: {m1.payload}")
        second_done.wait(timeout=5)
        print(email_lines[1])
        print(analytics_lines[1])

        broker.unsubscribe("orders", "analytics-service")
        print("Unsubscribed analytics-service")

        last_done = Event()

        def email_after_unsubscribe(message: Message) -> None:
            email_lines.append(_format("email-service", message))
            last_done.set()

        broker.subscribe("orders", "email-service", email_after_unsubscribe)
        m2 = broker.publish("orders", "order-3-shipped")
        print(f"Published orders#{m2.offset}: {m2.payload}")
        last_done.wait(timeout=5)
        print(email_lines[2])
    finally:
        broker.shutdown()


if __name__ == "__main__":
    main()
