"""Parking Lot — a self-contained LLD MVP.

This package mirrors the Java implementation one-to-one so the two can be compared:

    models.py      -> SpotType, VehicleType, Vehicle hierarchy, ParkingSpot, Ticket
    strategies.py  -> FeeStrategy / HourlyFeeStrategy, SpotAssignmentStrategy / NearestFirst
    factory.py     -> VehicleFactory
    exceptions.py  -> NoAvailableSpotError, InvalidTicketError
    lot.py         -> Level, ParkingLot, Receipt (the orchestrating service)

Concurrency: ParkingSpot uses a threading.Lock so "check free + claim" is atomic; the ParkingLot
keeps active tickets in a dict guarded by its own lock.
"""
