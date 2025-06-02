package org.example.domain;

import lombok.Value;
import lombok.Builder;
import org.example.domain.enums.NotificationStatus;

import java.util.UUID;
import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class NotificationDTO {
    UUID notificationId;
    UUID userId;
    UUID itemId;
    String message;
    LocalDateTime sentTime;
    NotificationStatus status;
    LocalDateTime readAt;
}