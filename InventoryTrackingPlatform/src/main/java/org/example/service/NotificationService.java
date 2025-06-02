package org.example.service;

import org.example.domain.NotificationDTO;
import org.example.domain.enums.NotificationStatus;
import org.example.entity.ItemEntity;
import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<NotificationDTO> findNotifications(UUID userId, NotificationStatus status);
    NotificationDTO markAsRead(UUID id);
    void createCriticalStockNotification(ItemEntity item);
}