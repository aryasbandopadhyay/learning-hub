"""The orchestrating service: PubSubBroker and per-subscriber subscriptions."""

from __future__ import annotations

import queue
import threading
from collections.abc import Callable
from datetime import datetime
from typing import TypeAlias

from .models import Message, Topic, utc_now

Subscriber: TypeAlias = Callable[[Message], None]


class _Subscription:
    """Runtime state for one subscriber attached to one topic.

    Each subscription owns a bounded queue and exactly one worker thread. That gives FIFO delivery
    for this subscriber, isolates slow subscribers from each other, and creates simple backpressure:
    when this queue is full, publishers block while enqueuing to this subscriber instead of dropping
    messages. The worker advances ``next_offset`` only after the callback returns successfully.
    """

    def __init__(self, topic_name: str, subscriber_id: str, subscriber: Subscriber, capacity: int) -> None:
        self.subscriber_id = subscriber_id
        self._subscriber = subscriber
        self._queue: queue.Queue[Message] = queue.Queue(maxsize=capacity)
        self._running = threading.Event()
        self._running.set()
        self._next_offset = 0
        self._offset_lock = threading.Lock()
        self._worker = threading.Thread(
            target=self._dispatch_loop,
            name=f"pubsub-{topic_name}-{subscriber_id}",
            daemon=True,
        )
        self._worker.start()

    def enqueue(self, message: Message) -> None:
        """Enqueue one message, waiting if this subscriber's bounded queue is full."""
        if self._running.is_set():
            self._queue.put(message)

    def stop(self) -> None:
        """Stop future delivery and let the daemon worker leave its polling loop."""
        self._running.clear()

    @property
    def next_offset(self) -> int:
        with self._offset_lock:
            return self._next_offset

    def _dispatch_loop(self) -> None:
        while self._running.is_set() or not self._queue.empty():
            try:
                message = self._queue.get(timeout=0.1)
            except queue.Empty:
                continue
            if not self._running.is_set():
                self._queue.task_done()
                continue
            self._subscriber(message)
            with self._offset_lock:
                self._next_offset = max(self._next_offset, message.offset + 1)
            self._queue.task_done()


class PubSubBroker:
    """Application service / facade for the in-memory pub-sub system.

    Observer pattern: subscribers register callbacks for a topic. Publishers only know the topic
    name and payload; the broker notifies all current observers.

    Concurrency: a broker-level lock protects topic/subscription map mutations. Publishing snapshots
    the active subscriptions under that lock, then releases it before enqueueing, so callbacks never
    run under the broker lock. Each subscriber has its own queue, worker thread, and offset.
    """

    def __init__(self, clock: Callable[[], datetime] = utc_now, queue_capacity: int = 1024) -> None:
        if queue_capacity <= 0:
            raise ValueError("queue_capacity must be positive")
        self._clock = clock
        self._queue_capacity = queue_capacity
        self._topics: dict[str, Topic] = {}
        self._subscriptions: dict[str, dict[str, _Subscription]] = {}
        self._lock = threading.Lock()

    def create_topic(self, name: str) -> Topic:
        """Create or return an existing topic. Idempotent, which keeps demos/tests simple."""
        with self._lock:
            topic = self._topics.get(name)
            if topic is None:
                topic = Topic(name)
                self._topics[name] = topic
            return topic

    def subscribe(self, topic_name: str, subscriber_id: str, subscriber: Subscriber) -> None:
        """Register/replace a subscriber for a topic. New subscribers receive future messages only."""
        with self._lock:
            if topic_name not in self._topics:
                self._topics[topic_name] = Topic(topic_name)
            by_subscriber = self._subscriptions.setdefault(topic_name, {})
            old = by_subscriber.get(subscriber_id)
            replacement = _Subscription(topic_name, subscriber_id, subscriber, self._queue_capacity)
            by_subscriber[subscriber_id] = replacement
        if old is not None:
            old.stop()

    def unsubscribe(self, topic_name: str, subscriber_id: str) -> None:
        """Remove a subscriber and stop its background dispatcher. Future publishes skip it."""
        with self._lock:
            removed = self._subscriptions.get(topic_name, {}).pop(subscriber_id, None)
        if removed is not None:
            removed.stop()

    def publish(self, topic_name: str, payload: str) -> Message:
        """Publish one payload to a topic and enqueue it for every active subscriber."""
        topic = self.create_topic(topic_name)
        message = topic.append(payload, self._clock)
        with self._lock:
            subscribers = tuple(self._subscriptions.get(topic_name, {}).values())
        for subscription in subscribers:
            subscription.enqueue(message)
        return message

    def next_offset(self, topic_name: str, subscriber_id: str) -> int:
        """Test/introspection helper: where the subscriber will read next after delivered callbacks."""
        with self._lock:
            subscription = self._subscriptions.get(topic_name, {}).get(subscriber_id)
        return -1 if subscription is None else subscription.next_offset

    def shutdown(self) -> None:
        """Stop every dispatcher thread; important for tests so the interpreter can exit cleanly."""
        with self._lock:
            subscriptions = [s for by_subscriber in self._subscriptions.values() for s in by_subscriber.values()]
            self._subscriptions.clear()
        for subscription in subscriptions:
            subscription.stop()

    def __enter__(self) -> "PubSubBroker":
        return self

    def __exit__(self, exc_type, exc, tb) -> None:  # pragma: no cover - convenience wrapper
        self.shutdown()
