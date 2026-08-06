"""Vending Machine — a self-contained LLD MVP.

This package mirrors the Java implementation one-to-one:

    models.py      -> Product, InventoryItem, result DTOs, MachineStateName
    states.py      -> State pattern: Idle, HasMoney, Dispensing, SoldOut
    machine.py     -> VendingMachine aggregate root + transaction lock
    exceptions.py  -> clear domain errors for invalid operations
    main.py        -> runnable demo

Concurrency: VendingMachine uses an RLock around every public operation. The ``purchase`` helper
keeps that lock for a full customer transaction, so concurrent buyers cannot oversell the last unit.
"""
