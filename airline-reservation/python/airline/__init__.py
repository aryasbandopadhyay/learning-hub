"""Airline Reservation — a self-contained LLD MVP.

This package mirrors the Java implementation one-to-one:

    models.py      -> Cabin, SeatStatus, Passenger, Seat, FlightInventory, Flight, Booking
    strategies.py  -> CabinPricingStrategy / FixedCabinPricingStrategy
    exceptions.py  -> domain-specific errors
    service.py     -> AirlineReservationService (search, book, cancel)
    main.py        -> runnable demo

Concurrency: Seat uses a threading.Lock so "check AVAILABLE + mark BOOKED" is atomic; the service
keeps PNRs in a dict guarded by its own lock.
"""
