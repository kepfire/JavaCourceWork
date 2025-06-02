package org.example.event;

import java.util.UUID;

public record NotificationEvent(String message, String type, UUID itemId) { }
