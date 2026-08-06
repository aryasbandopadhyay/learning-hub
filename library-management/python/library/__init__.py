"""Library Management — a self-contained LLD MVP.

This package mirrors the Java implementation one-to-one so the two can be compared:

    models.py      -> Book, BookItem, Member, Loan, BookItemStatus
    strategies.py  -> FineStrategy / PerDayFineStrategy
    exceptions.py  -> NoAvailableCopyError, LoanLimitExceededError, InvalidLoanError
    service.py     -> LibraryService, ReturnReceipt (the orchestrating service)

Concurrency: BookItem uses a threading.Lock so "check available + mark loaned" is atomic; the
LibraryService keeps active loans in a dict guarded by its own lock.
"""
