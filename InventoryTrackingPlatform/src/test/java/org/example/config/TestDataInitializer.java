package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.domain.enums.NotificationStatus;
import org.example.domain.enums.OperationSource;
import org.example.domain.enums.OperationType;
import org.example.domain.enums.Role;
import org.example.entity.ItemEntity;
import org.example.entity.NotificationEntity;
import org.example.entity.OperationEntity;
import org.example.entity.UserEntity;
import org.example.repository.ItemRepository;
import org.example.repository.NotificationRepository;
import org.example.repository.OperationRepository;
import org.example.repository.UserRepository;
import org.example.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataInitializer {

    private final JwtTokenProvider jwtProvider;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OperationRepository operationRepository;
    private final NotificationRepository notificationRepository;

    public TestData initTestData() {
        notificationRepository.deleteAll();
        operationRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity adminUser = userRepository.save(UserEntity.builder()
                .email("admin@gmail.com")
                .passwordHash("securepassword")
                .username("Admin")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build());

        String adminToken = jwtProvider.createToken(adminUser.getEmail(), adminUser.getRole());

        UserEntity regularUser = userRepository.save(UserEntity.builder()
                .email("user@gmail.com")
                .passwordHash("securepassword")
                .username("User")
                .role(Role.MANAGER)
                .createdAt(LocalDateTime.now())
                .build());

        String userToken = jwtProvider.createToken(regularUser.getEmail(), regularUser.getRole());

        ItemEntity item = itemRepository.save(ItemEntity.builder()
                .name("Laptop")
                .category("Electronics")
                .currentQuantity(10)
                .criticalQuantity(2)
                .supplier("TechSupplier Ltd.")
                .barcode("123456789")
                .description("High-performance laptop")
                .createdAt(LocalDateTime.now())
                .build());

        OperationEntity operation = operationRepository.save(OperationEntity.builder()
                .item(item)
                .user(regularUser)
                .operationType(OperationType.INCOMING)
                .quantity(5)
                .previousQuantity(5)
                .source(OperationSource.MANUAL)
                .timestamp(LocalDateTime.now())
                .build());

        NotificationEntity notification = notificationRepository.save(NotificationEntity.builder()
                .user(regularUser)
                .item(item)
                .message("Stock updated for Laptop")
                .type("STOCK_ALERT")
                .sentTime(LocalDateTime.now())
                .status(NotificationStatus.UNREAD)
                .build());

        return new TestData(adminUser.getUserId(), regularUser.getUserId(), item.getItemId(), operation.getOperationId(), notification.getNotificationId(), adminToken, userToken);
    }

    public record TestData(UUID adminId, UUID userId, UUID itemId, UUID operationId, UUID notificationId, String adminToken, String userToken) {}
}
