"""The orchestrating service: LibraryService and ReturnReceipt."""

from __future__ import annotations

import threading
from dataclasses import dataclass
from datetime import datetime, timedelta, timezone
from typing import Callable, Sequence

from .exceptions import InvalidLoanError, LoanLimitExceededError, NoAvailableCopyError
from .models import Book, Loan, Member
from .strategies import FineStrategy


@dataclass(frozen=True)
class ReturnReceipt:
    loan: Loan
    return_time: datetime
    fine: int


def _utc_now() -> datetime:
    return datetime.now(timezone.utc)


class LibraryService:
    """Aggregate root wiring catalog + loan state + fine strategy.

    Depends only on the FineStrategy abstraction (Dependency Inversion). A ``clock`` callable is
    injected so due dates and fine tests are deterministic.

    Concurrency: a service lock protects member-limit counting and active loan updates. The physical
    copy remains the important boundary because ``BookItem.try_checkout`` atomically changes
    AVAILABLE -> LOANED and prevents double-loaning the same barcode.
    """

    def __init__(
        self,
        catalog: Sequence[Book],
        loan_period: timedelta,
        fine_strategy: FineStrategy,
        clock: Callable[[], datetime] = _utc_now,
    ) -> None:
        self._catalog: tuple[Book, ...] = tuple(catalog)
        self._loan_period = loan_period
        self._fine = fine_strategy
        self._clock = clock
        self._active_loans: dict[str, Loan] = {}
        self._lock = threading.Lock()

    def search(self, query: str) -> list[Book]:
        """Case-insensitive substring search across title and author."""
        q = query.lower()
        return [b for b in self._catalog if q in b.title.lower() or q in b.author.lower()]

    def checkout(self, member: Member, book: Book) -> Loan:
        """Claim one available copy and create a Loan, while enforcing member max-loans."""
        with self._lock:
            member_loans = sum(1 for loan in self._active_loans.values() if loan.member.id == member.id)
            if member_loans >= member.max_concurrent_loans:
                raise LoanLimitExceededError(f"{member.name} reached the loan limit")

            for item in book.items:
                if item.try_checkout():
                    checkout_time = self._clock()
                    loan = Loan(member, item, checkout_time, checkout_time + self._loan_period)
                    self._active_loans[loan.id] = loan
                    return loan

        raise NoAvailableCopyError(f"No available copy for {book.title}")

    def return_loan(self, loan_id: str) -> ReturnReceipt:
        """Close the loan, compute fine, and mark the copy available again."""
        with self._lock:
            loan = self._active_loans.pop(loan_id, None)
        if loan is None:
            raise InvalidLoanError(f"Unknown or already-returned loan: {loan_id}")
        return_time = self._clock()
        fine = self._fine.calculate_fine(loan, return_time)
        loan.close(return_time)
        loan.item.mark_available()
        return ReturnReceipt(loan=loan, return_time=return_time, fine=fine)

    def active_loan_count(self, member: Member) -> int:
        with self._lock:
            return sum(1 for loan in self._active_loans.values() if loan.member.id == member.id)

    @property
    def catalog(self) -> tuple[Book, ...]:
        return self._catalog
