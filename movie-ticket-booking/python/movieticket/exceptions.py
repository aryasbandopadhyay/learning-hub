"""Domain exceptions for the booking service."""


class SeatUnavailableError(RuntimeError):
    pass


class BookingNotFoundError(RuntimeError):
    pass


class InvalidBookingStateError(RuntimeError):
    pass


class PaymentRejectedError(RuntimeError):
    pass
