package org.example.event.listener;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.enums.NotificationStatus;
import org.example.domain.enums.Role;
import org.example.entity.ItemEntity;
import org.example.entity.NotificationEntity;
import org.example.entity.UserEntity;
import org.example.event.NotificationEvent;
import org.example.repository.NotificationRepository;
import org.example.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @EventListener
    @Transactional
    public void handleNotificationEvent(NotificationEvent event) {
        List<UserEntity> users = userRepository.findByRole(Role.MANAGER);
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("No users found with role MANAGER");
        }

        users.forEach(user -> {
            NotificationEntity notification = NotificationEntity.builder()
                    .message(event.message())
                    .type(event.type())
                    .user(user)
                    .item(event.itemId() != null ? new ItemEntity(event.itemId()) : null)
                    .status(NotificationStatus.UNREAD)
                    .sentTime(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
        });
    }
}
