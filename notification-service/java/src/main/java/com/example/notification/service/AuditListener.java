package com.example.notification.service;

import com.example.notification.model.NotificationEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Thread-safe observer useful for audit trails and tests. */
public class AuditListener implements NotificationListener {

    private final CopyOnWriteArrayList<NotificationEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public void onEvent(NotificationEvent event) {
        events.add(event);
    }

    public List<NotificationEvent> getEvents() {
        return List.copyOf(events);
    }
}
