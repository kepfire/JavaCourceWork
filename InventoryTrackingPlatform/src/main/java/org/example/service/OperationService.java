package org.example.service;

import org.example.domain.OperationDTO;
import org.example.domain.enums.OperationType;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationService {
    OperationDTO createManualOperation(OperationDTO operationDTO);
    List<OperationDTO> findOperations(OperationType operationType);
}