"""Splitwise — a self-contained LLD MVP.

This package mirrors the Java implementation one-to-one:

    models.py      -> User, Split, Expense, BalanceSummary
    strategies.py  -> SplitStrategy plus Equal/Exact/Percent implementations
    service.py     -> ExpenseManager, the thread-safe balance-sheet service
    exceptions.py  -> InvalidSplitError
    main.py        -> runnable demo

Money is represented as integer cents, never float, so balances are exact. The service uses a
threading.RLock around add_expense because it mutates shared expenses + balances together.
"""
