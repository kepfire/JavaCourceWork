package org.example.service;

import org.example.domain.OperationDTO;
import org.example.domain.enums.OperationType;
import org.example.entity.ItemEntity;
import org.example.entity.OperationEntity;
import org.example.entity.UserEntity;
import org.example.event.NotificationEvent;
import org.example.repository.ItemRepository;
import org.example.repository.OperationRepository;
import org.example.repository.UserRepository;
import org.example.service.impl.OperationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperationServiceTest {
    @Mock
    private OperationRepository operationRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    private OperationServiceImpl operationService;

    private UUID itemId;
    private UUID userId;
    private ItemEntity item;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        operationService = new OperationServiceImpl(operationRepository, itemRepository, userRepository, eventPublisher);

        itemId = UUID.randomUUID();
        userId = UUID.randomUUID();

        item = new ItemEntity();
        item.setItemId(itemId);
        item.setName("Test Item");
        item.setCurrentQuantity(10);
        item.setCriticalQuantity(15);

        user = new UserEntity();
        user.setUserId(userId);
    }

    @Test
    void testCreateManualOperation_CriticalStock() {
        OperationDTO operationDTO = OperationDTO.builder()
                .itemId(itemId)
                .userId(userId)
                .operationType(OperationType.INCOMING)
                .quantity(5)
                .build();

        OperationEntity operationEntity = OperationEntity.builder()
                .item(item)
                .user(user)
                .operationType(OperationType.INCOMING)
                .quantity(5)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(operationRepository.save(any())).thenReturn(operationEntity);

        OperationDTO result = operationService.createManualOperation(operationDTO);

        assertEquals(5, result.getQuantity());
        verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class));
    }
}
