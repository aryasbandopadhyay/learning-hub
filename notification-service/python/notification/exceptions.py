"""Domain-specific exceptions for channel delivery failures."""


class NotificationDeliveryError(RuntimeError):
    """Channel implementations raise this for transient/permanent provider failures."""
