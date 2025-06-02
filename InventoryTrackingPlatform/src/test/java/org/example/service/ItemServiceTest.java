package org.example.service;

import org.example.domain.ItemDTO;
import org.example.entity.ItemEntity;
import org.example.event.NotificationEvent;
import org.example.exception.ItemNotFoundException;
import org.example.repository.ItemRepository;
import org.example.service.impl.ItemServiceImpl;
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
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserService userService;
    @Mock
    private ApplicationEventPublisher eventPublisher;


    private ItemServiceImpl itemService;

    private UUID itemId;
    private ItemEntity item;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, notificationService, userService, eventPublisher);

        itemId = UUID.randomUUID();
        item = new ItemEntity();
        item.setItemId(itemId);
        item.setName("Test Item");
    }

    @Test
    void testFindById_ItemExists() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDTO result = itemService.findById(itemId);

        assertNotNull(result);
        assertEquals(itemId, result.getItemId());
        assertEquals("Test Item", result.getName());
    }

    @Test
    void testFindById_ItemNotFound() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findById(itemId));
    }

    @Test
    void testUpdateItem_Success() {
        ItemDTO updatedDTO = ItemDTO.builder()
                .itemId(itemId)
                .name("Updated Name")
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDTO result = itemService.updateItem(itemId, updatedDTO);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class));
    }
}
