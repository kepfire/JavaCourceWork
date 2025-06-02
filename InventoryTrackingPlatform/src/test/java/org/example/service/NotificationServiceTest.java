package org.example.service;

import org.example.domain.NotificationDTO;
import org.example.domain.enums.NotificationStatus;
import org.example.entity.NotificationEntity;
import org.example.entity.UserEntity;
import org.example.repository.NotificationRepository;
import org.example.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserService userService;
    private NotificationServiceImpl notificationService;

    private UUID userId;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(notificationRepository, userService);

        userId = UUID.randomUUID();
        user = new UserEntity();
        user.setUserId(userId);
    }

    @Test
    void testFindNotifications_Success() {
        NotificationEntity notification = new NotificationEntity();
        notification.setNotificationId(UUID.randomUUID());
        notification.setUser(user);
        notification.setMessage("New notification");

        when(notificationRepository.findByUserAndStatus(userId, NotificationStatus.UNREAD)).thenReturn(List.of(notification));

        List<NotificationDTO> notifications = notificationService.findNotifications(userId, NotificationStatus.UNREAD);

        assertFalse(notifications.isEmpty());
        assertEquals("New notification", notifications.get(0).getMessage());
    }

    @Test
    void testMarkAsRead_Success() {
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        NotificationEntity notification = new NotificationEntity();
        notification.setNotificationId(notificationId);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setUser(user);

        NotificationEntity updatedNotification = new NotificationEntity();
        updatedNotification.setNotificationId(notificationId);
        updatedNotification.setStatus(NotificationStatus.READ);
        updatedNotification.setUser(user);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any())).thenReturn(updatedNotification);

        NotificationDTO result = notificationService.markAsRead(notificationId);

        assertEquals(NotificationStatus.READ, result.getStatus());
        assertEquals(userId, result.getUserId());
    }

}
