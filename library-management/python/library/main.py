"""Runnable demo: build catalog, search, checkout, return, print receipts.

Run:  python -m library.main   (from the python/ directory)
"""

from __future__ import annotations

from datetime import timedelta

from .models import Book, BookItem, Member
from .service import LibraryService
from .strategies import PerDayFineStrategy


def main() -> None:
    clean_code = Book("9780132350884", "Clean Code", "Robert C. Martin")
    clean_code.add_item(BookItem("BC-001", clean_code))
    clean_code.add_item(BookItem("BC-002", clean_code))
    design_patterns = Book("9780201633610", "Design Patterns", "Erich Gamma")
    design_patterns.add_item(BookItem("DP-001", design_patterns))

    library = LibraryService(
        [clean_code, design_patterns],
        timedelta(days=14),
        PerDayFineStrategy(rate_per_day=5),
    )
    member = Member("M1", "Asha", 2)

    print("Catalog size:", len(library.catalog))
    print("Search 'code':", len(library.search("code")), "book(s)")

    loan = library.checkout(member, clean_code)
    print(f"Checked out {loan.item.barcode} to {member.name}")
    print("Due in days: 14")

    receipt = library.return_loan(loan.id)
    print(f"Returned {receipt.loan.item.barcode}, fine = {receipt.fine}")
    print("Active loans for member:", library.active_loan_count(member))


if __name__ == "__main__":
    main()
