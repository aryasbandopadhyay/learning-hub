"""Domain-specific exceptions for clear test assertions and caller handling."""


class RoomUnavailableError(Exception):
    pass


class RoomNotFoundError(Exception):
    pass


class ReservationNotFoundError(Exception):
    pass


class InvalidReservationStateError(Exception):
    pass
