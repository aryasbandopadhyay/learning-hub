"""End-to-end tests for the Library Management MVP, including a concurrency race test."""

from __future__ import annotations

import threading
from datetime import datetime, timedelta, timezone

import pytest

from library.exceptions import LoanLimitExceededError, NoAvailableCopyError
from library.models import Book, BookItem, BookItemStatus, Member
from library.service import LibraryService
from library.strategies import PerDayFineStrategy


class MutableClock:
    """Hand-advanced clock so due-date/fine tests are deterministic (no sleeps)."""

    def __init__(self, start: datetime) -> None:
        self._now = start

    def __call__(self) -> datetime:
        return self._now

    def advance(self, delta: timedelta) -> None:
        self._now += delta


def make_book(isbn: str, title: str, author: str, *barcodes: str) -> Book:
    book = Book(isbn, title, author)
    for barcode in barcodes:
        book.add_item(BookItem(barcode, book))
    return book


def make_library(clock=None, *books: Book) -> LibraryService:
    return LibraryService(
        list(books),
        timedelta(days=14),
        PerDayFineStrategy(rate_per_day=5),
        clock or (lambda: datetime.now(timezone.utc)),
    )


def test_search_by_title_and_author_returns_expected_books():
    clean_code = make_book("9780132350884", "Clean Code", "Robert C. Martin", "BC-001")
    domain_driven = make_book("9780321125217", "Domain-Driven Design", "Eric Evans", "DD-001")
    library = make_library(None, clean_code, domain_driven)

    assert library.search("code") == [clean_code]
    assert library.search("evans") == [domain_driven]


def test_checkout_marks_copy_loaned_creates_loan_and_fails_when_last_copy_gone():
    clock = MutableClock(datetime(2024, 1, 1, 10, 0, tzinfo=timezone.utc))
    clean_code = make_book("9780132350884", "Clean Code", "Robert C. Martin", "BC-001")
    library = make_library(clock, clean_code)
    asha = Member("M1", "Asha", 2)
    ben = Member("M2", "Ben", 2)

    loan = library.checkout(asha, clean_code)

    assert loan.item.status is BookItemStatus.LOANED
    assert loan.due_time == datetime(2024, 1, 15, 10, 0, tzinfo=timezone.utc)
    with pytest.raises(NoAvailableCopyError):
        library.checkout(ben, clean_code)


def test_per_member_limit_is_enforced():
    clean_code = make_book("9780132350884", "Clean Code", "Robert C. Martin", "BC-001")
    refactoring = make_book("9780201485677", "Refactoring", "Martin Fowler", "RF-001")
    library = make_library(None, clean_code, refactoring)
    asha = Member("M1", "Asha", 1)

    library.checkout(asha, clean_code)

    with pytest.raises(LoanLimitExceededError):
        library.checkout(asha, refactoring)


def test_return_frees_copy_and_computes_overdue_fine():
    clock = MutableClock(datetime(2024, 1, 1, 10, 0, tzinfo=timezone.utc))
    clean_code = make_book("9780132350884", "Clean Code", "Robert C. Martin", "BC-001")
    library = make_library(clock, clean_code)
    asha = Member("M1", "Asha", 2)

    loan = library.checkout(asha, clean_code)
    clock.advance(timedelta(days=17))  # 14-day loan period + 3 days late

    receipt = library.return_loan(loan.id)
    assert receipt.fine == 3 * 5
    assert loan.item.status is BookItemStatus.AVAILABLE
    assert library.active_loan_count(asha) == 0


def test_concurrent_checkout_never_double_loans_single_copy():
    threads = 50
    clean_code = make_book("9780132350884", "Clean Code", "Robert C. Martin", "BC-001")
    library = make_library(None, clean_code)

    start = threading.Event()
    successes: list[str] = []
    successes_lock = threading.Lock()

    def worker(i: int) -> None:
        start.wait()  # release all threads together for maximum contention
        try:
            loan = library.checkout(Member(f"M{i}", f"Member {i}", 1), clean_code)
            with successes_lock:
                successes.append(loan.item.barcode)
        except NoAvailableCopyError:
            pass  # expected for the losers

    workers = [threading.Thread(target=worker, args=(i,)) for i in range(threads)]
    for w in workers:
        w.start()
    start.set()
    for w in workers:
        w.join(timeout=10)

    assert len(successes) == 1, "exactly one member should borrow the single copy"
    assert len(set(successes)) == 1, "the copy barcode is unique"
    assert clean_code.items[0].status is BookItemStatus.LOANED
