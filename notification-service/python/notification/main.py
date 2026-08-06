"""Runnable demo: send one notification and print captured deliveries.

Run:  python -m notification.main   (from the python/ directory)
"""

from __future__ import annotations

from .channels import ChannelFactory, EmailChannel, PushChannel
from .models import ChannelType, User
from .service import AuditListener, NotificationService


def main() -> None:
    factory = ChannelFactory()
    service = NotificationService(factory, max_attempts=3)
    audit = AuditListener()
    service.register_listener(audit)

    user = User(
        id="u1",
        name="Asha",
        email="asha@example.com",
        phone_number="+919999999999",
        device_token="device-123",
        preferred_channels=frozenset({ChannelType.EMAIL, ChannelType.PUSH}),
    )

    results = service.notify(user, "Your order has shipped")
    email = factory.create(ChannelType.EMAIL)
    push = factory.create(ChannelType.PUSH)
    assert isinstance(email, EmailChannel)
    assert isinstance(push, PushChannel)

    print("Results:", {k.value: v.status.value for k, v in results.items()})
    print("Email sent:", len(email.sent_messages))
    print("Push sent:", len(push.sent_messages))
    print("Audit events:", len(audit.events))
    print("Channels used:", [c.value for c in results])


if __name__ == "__main__":
    main()
