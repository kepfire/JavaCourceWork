package org.example.IT;

import org.example.config.TestDataInitializer;
import org.example.domain.NotificationDTO;
import org.example.domain.enums.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotificationControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataInitializer dataInitializer;

    private String adminToken;
    private UUID notificationId;

    @BeforeEach
    void setup() {
        TestDataInitializer.TestData testData = dataInitializer.initTestData();
        notificationId = testData.notificationId();
        adminToken = testData.adminToken();
    }

    @Test
    void shouldGetUserNotifications() {
        webTestClient.get()
                .uri("/api/v1/notifications?userId={userId}", dataInitializer.initTestData().userId())
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(NotificationDTO.class)
                .consumeWith(response -> {
                    List<NotificationDTO> notifications = response.getResponseBody();
                    assertNotNull(notifications);
                    assertFalse(notifications.isEmpty());
                });
    }

    @Test
    void shouldGetFilteredNotifications() {
        webTestClient.get()
                .uri("/api/v1/notifications?userId={userId}&status=UNREAD", dataInitializer.initTestData().userId())
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(NotificationDTO.class)
                .consumeWith(response -> {
                    List<NotificationDTO> notifications = response.getResponseBody();
                    assertNotNull(notifications);
                    assertFalse(notifications.isEmpty());
                    assertTrue(notifications.stream().allMatch(n -> NotificationStatus.UNREAD.equals(n.getStatus())));
                });
    }

    @Test
    void shouldMarkNotificationAsRead() {
        webTestClient.patch()
                .uri("/api/v1/notifications/{id}/read", notificationId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(NotificationDTO.class)
                .consumeWith(response -> {
                    NotificationDTO notification = response.getResponseBody();
                    assertNotNull(notification);
                    assertEquals(NotificationStatus.READ, notification.getStatus());
                });
    }

    @Test
    void shouldFailToMarkNonexistentNotification() {
        UUID nonexistentNotificationId = UUID.randomUUID();

        webTestClient.patch()
                .uri("/api/v1/notifications/{id}/read", nonexistentNotificationId)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Notification not found with id: " + nonexistentNotificationId);
    }
}
