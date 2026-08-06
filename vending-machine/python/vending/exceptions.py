"""Domain exceptions for clear machine-coding failure cases."""


class InvalidDenominationError(Exception):
    """A coin is not accepted by this machine."""


class InvalidStateError(Exception):
    """An operation is not valid in the current State object."""


class InsufficientFundsError(Exception):
    """The current balance is less than the selected product price."""


class OutOfStockError(Exception):
    """The selected product exists but has zero stock."""


class UnknownProductError(Exception):
    """The selected product code is not in the catalog."""
