"""Notification Service — a self-contained LLD MVP.

This package mirrors the Java implementation one-to-one:

    models.py      -> ChannelType, User, SentMessage, NotificationResult/Event
    channels.py    -> NotificationChannel strategy implementations + ChannelFactory
    service.py     -> NotificationService, NotificationListener, AuditListener
    exceptions.py  -> NotificationDeliveryError
    main.py        -> runnable demo

Concurrency: channel sinks and audit listeners use locks; service listener registration and observer
snapshots are locked so multiple threads can send without dropped or duplicated records.
"""
