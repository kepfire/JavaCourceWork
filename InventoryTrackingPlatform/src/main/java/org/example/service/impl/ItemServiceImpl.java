package org.example.service.impl;

import org.example.domain.ItemDTO;
import org.example.entity.ItemEntity;
import org.example.event.NotificationEvent;
import org.example.exception.ItemNotFoundException;
import org.example.repository.ItemRepository;
import org.example.service.ItemService;
import org.example.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.example.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> findAllItems(String category, String supplier, String sortBy) {
        return itemRepository.findWithFilters(category, supplier, sortBy)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDTO findById(UUID id) {
        return itemRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> findCriticalItems() {
        return itemRepository.findByCurrentQuantityLessThanCritical()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDTO createItem(ItemDTO itemDTO) {
        ItemEntity entity = mapToEntity(itemDTO);
        return mapToDTO(itemRepository.save(entity));
    }

    @Override
    @Transactional
    public ItemDTO updateItem(UUID id, ItemDTO itemDTO) {
        ItemEntity entity = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
        updateEntity(itemDTO, entity);
        ItemDTO updatedDTO = mapToDTO(itemRepository.save(entity));

        eventPublisher.publishEvent(new NotificationEvent(
                "Product successfully updated: " + updatedDTO.getName(),
                "ITEM_UPDATE",
                updatedDTO.getItemId()));

        return updatedDTO;
    }

    @Override
    @Transactional
    public void deleteItem(UUID id) {
        ItemEntity entity = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
        itemRepository.delete(entity);
    }

    @Override
    @Transactional
    public void checkStockLevels() {
        itemRepository.findByCurrentQuantityLessThanCritical()
                .forEach(notificationService::createCriticalStockNotification);
    }

    private ItemDTO mapToDTO(ItemEntity entity) {
        return ItemDTO.builder()
                .itemId(entity.getItemId())
                .name(entity.getName())
                .category(entity.getCategory())
                .currentQuantity(entity.getCurrentQuantity())
                .criticalQuantity(entity.getCriticalQuantity())
                .supplier(entity.getSupplier())
                .description(entity.getDescription())
                .barcode(entity.getBarcode())
                .build();
    }

    private ItemEntity mapToEntity(ItemDTO dto) {
        return ItemEntity.builder()
                .itemId(dto.getItemId())
                .name(dto.getName())
                .category(dto.getCategory())
                .currentQuantity(dto.getCurrentQuantity())
                .criticalQuantity(dto.getCriticalQuantity())
                .supplier(dto.getSupplier())
                .description(dto.getDescription())
                .barcode(dto.getBarcode())
                .build();
    }

    private void updateEntity(ItemDTO dto, ItemEntity entity) {
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setCurrentQuantity(dto.getCurrentQuantity());
        entity.setCriticalQuantity(dto.getCriticalQuantity());
        entity.setSupplier(dto.getSupplier());
        entity.setDescription(dto.getDescription());
        entity.setBarcode(dto.getBarcode());
    }
}