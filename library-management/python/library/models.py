"""Domain models: catalog title, physical copy, member, and loan."""

from __future__ import annotations

import threading
import uuid
from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum
from typing import Optional


class BookItemStatus(Enum):
    """Physical-copy state kept intentionally small for the MVP."""

    AVAILABLE = "AVAILABLE"
    LOANED = "LOANED"


class Book:
    """Bibliographic title-level entity shared by many physical copies.

    Relationship: Book 1..* BookItem. Metadata such as publisher/edition is deliberately omitted
    because checkout behavior depends on copy availability, not on extra catalog fields.
    """

    def __init__(self, isbn: str, title: str, author: str) -> None:
        self.isbn = isbn
        self.title = title
        self.author = author
        self._items: list[BookItem] = []

    def add_item(self, item: "BookItem") -> None:
        """Attach a physical copy to this catalog title."""
        self._items.append(item)

    @property
    def items(self) -> tuple["BookItem", ...]:
        return tuple(self._items)

    def __repr__(self) -> str:  # pragma: no cover - trivial
        return f"{self.title} by {self.author} ({self.isbn})"


class BookItem:
    """A physical copy of a Book. THE CONCURRENCY BOUNDARY.

    ``try_checkout`` and ``mark_available`` hold ``self._lock`` so the "is this copy available?"
    check and the state change are one atomic step. When many members race for the last copy, the
    lock guarantees exactly one flips AVAILABLE to LOANED.
    """

    def __init__(self, barcode: str, book: Book) -> None:
        self.barcode = barcode
        self.book = book
        self._status = BookItemStatus.AVAILABLE
        self._lock = threading.Lock()

    def try_checkout(self) -> bool:
        """Atomically claim this copy; return True on success."""
        with self._lock:
            if self._status is not BookItemStatus.AVAILABLE:
                return False
            self._status = BookItemStatus.LOANED
            return True

    def mark_available(self) -> None:
        with self._lock:
            self._status = BookItemStatus.AVAILABLE

    @property
    def status(self) -> BookItemStatus:
        with self._lock:
            return self._status


@dataclass(frozen=True)
class Member:
    """Borrower entity. The service owns active-loan counting for this member."""

    id: str
    name: str
    max_concurrent_loans: int


@dataclass
class Loan:
    """Join entity connecting Member <-> BookItem for one borrowing period.

    Relationship: Member 1..* Loan and BookItem 1..* historical Loan (only one active at a time).
    ``return_time`` stays None until check-in.
    """

    member: Member
    item: BookItem
    checkout_time: datetime
    due_time: datetime
    id: str = field(default_factory=lambda: str(uuid.uuid4()))
    return_time: Optional[datetime] = None

    @property
    def is_open(self) -> bool:
        return self.return_time is None

    def close(self, return_time: datetime) -> None:
        self.return_time = return_time
