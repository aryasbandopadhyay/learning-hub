package com.example.notification.service;

import com.example.notification.model.NotificationEvent;

/** Observer pattern: listeners react to final send outcomes without changing NotificationService. */
@FunctionalInterface
public interface NotificationListener {

    void onEvent(NotificationEvent event);
}
