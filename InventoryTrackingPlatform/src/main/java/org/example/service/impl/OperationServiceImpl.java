package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.OperationDTO;
import org.example.domain.enums.OperationType;
import org.example.entity.ItemEntity;
import org.example.entity.OperationEntity;
import org.example.entity.UserEntity;
import org.example.event.NotificationEvent;
import org.example.repository.ItemRepository;
import org.example.repository.OperationRepository;
import org.example.repository.UserRepository;
import org.example.service.OperationService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public OperationDTO createManualOperation(OperationDTO operationDTO) {
        OperationEntity entity = mapToEntity(operationDTO);
        int previousQuantity = entity.getItem().getCurrentQuantity();

        updateItemQuantity(entity);

        int updatedQuantity = entity.getItem().getCurrentQuantity();

        OperationDTO savedDTO = mapToDTO(operationRepository.save(entity));

        if (updatedQuantity <= entity.getItem().getCriticalQuantity() &&
                previousQuantity > entity.getItem().getCriticalQuantity()) {
            eventPublisher.publishEvent(new NotificationEvent(
                    "Critical stock balance: " + entity.getItem().getName(),
                    "CRITICAL_STOCK",
                    entity.getItem().getItemId()));
        }

        return savedDTO;
    }

    private void updateItemQuantity(OperationEntity operation) {
        ItemEntity item = operation.getItem();
        int newQuantity = calculateNewQuantity(item, operation);
        item.setCurrentQuantity(newQuantity);
    }

    private int calculateNewQuantity(ItemEntity item, OperationEntity operation) {
        return switch (operation.getOperationType()) {
            case INCOMING -> item.getCurrentQuantity() + operation.getQuantity();
            case OUTGOING, WRITE_OFF -> item.getCurrentQuantity() - operation.getQuantity();
            case CORRECTION -> operation.getQuantity();
            default -> item.getCurrentQuantity();
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperationDTO> findOperations(OperationType operationType) {

        return operationRepository.findByFilters(operationType)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    private OperationDTO mapToDTO(OperationEntity entity) {
        return OperationDTO.builder()
                .operationId(entity.getOperationId())
                .itemId(entity.getItem().getItemId())
                .userId(entity.getUser().getUserId())
                .operationType(entity.getOperationType())
                .quantity(entity.getQuantity())
                .previousQuantity(entity.getPreviousQuantity() != null ? entity.getPreviousQuantity() : 0)
                .source(entity.getSource())
                .timestamp(entity.getTimestamp())
                .build();
    }

    private OperationEntity mapToEntity(OperationDTO dto) {
        ItemEntity item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + dto.getItemId()));
        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getUserId()));
        
        return OperationEntity.builder()
                .operationId(dto.getOperationId())
                .item(item)
                .user(user)
                .operationType(dto.getOperationType())
                .quantity(dto.getQuantity())
                .previousQuantity(dto.getPreviousQuantity())
                .source(dto.getSource())
                .timestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now())
                .build();
    }
}