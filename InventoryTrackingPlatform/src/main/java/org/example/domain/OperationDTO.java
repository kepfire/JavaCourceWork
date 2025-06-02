package org.example.domain;

import lombok.Value;
import lombok.Builder;
import org.example.domain.enums.OperationSource;
import org.example.domain.enums.OperationType;

import java.util.UUID;
import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class OperationDTO {
    UUID operationId;
    UUID itemId;
    UUID userId;
    OperationType operationType;
    int quantity;
    int previousQuantity;
    OperationSource source;
    LocalDateTime timestamp;
}