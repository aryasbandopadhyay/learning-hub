"""Domain exceptions for invalid elevator requests."""


class InvalidFloorError(ValueError):
    """Raised when a request references a floor outside the building."""


class ElevatorNotFoundError(ValueError):
    """Raised when a car-panel request references an unknown elevator car."""
