package org.example.service.impl;

import org.example.domain.NotificationDTO;
import org.example.domain.enums.Role;
import org.example.entity.NotificationEntity;
import org.example.entity.ItemEntity;
import org.example.entity.UserEntity;
import org.example.exception.NotificationNotFoundException;
import org.example.repository.NotificationRepository;
import org.example.service.NotificationService;
import org.example.service.UserService;
import org.example.domain.enums.NotificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> findNotifications(UUID userId, NotificationStatus status) {
        return notificationRepository.findByUserAndStatus(userId, status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationDTO markAsRead(UUID id) {
        NotificationEntity notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));

        notification.setStatus(NotificationStatus.READ);
        return mapToDTO(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void createCriticalStockNotification(ItemEntity item) {
        List<UserEntity> recipients = userService.findUsersByRole(Role.MANAGER);

        recipients.forEach(user -> {
            NotificationEntity notification = NotificationEntity.builder()
                    .message("Критичний залишок товару: " + item.getName())
                    .type("CRITICAL_STOCK")
                    .item(item)
                    .user(user)
                    .status(NotificationStatus.UNREAD)
                    .sentTime(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
        });
    }

    private NotificationDTO mapToDTO(NotificationEntity entity) {
        return NotificationDTO.builder()
                .notificationId(entity.getNotificationId())
                .userId(entity.getUser().getUserId())
                .itemId(entity.getItem() != null ? entity.getItem().getItemId() : null)
                .message(entity.getMessage())
                .sentTime(entity.getSentTime())
                .status(entity.getStatus())
                .build();
    }
}