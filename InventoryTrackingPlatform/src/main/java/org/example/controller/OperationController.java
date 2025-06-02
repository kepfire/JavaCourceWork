package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.OperationDTO;
import org.example.domain.enums.OperationType;
import org.example.service.OperationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/operations")
@RequiredArgsConstructor
@Tag(name = "Operations", description = "APIs for managing inventory operations")
public class OperationController {

    private final OperationService operationService;

    @PostMapping("/manual")
    @Operation(summary = "Create manual operation", description = "Creates a new manual inventory operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created operation"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public OperationDTO createManualOperation(@Parameter(description = "Operation details") @RequestBody OperationDTO operationDTO) {
        return operationService.createManualOperation(operationDTO);
    }

    @GetMapping
    @Operation(summary = "Get operations history", description = "Retrieves a list of operations with optional filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public List<OperationDTO> getOperationsHistory(
            @RequestParam(required = false) OperationType operationType) {
        return operationService.findOperations(operationType);
    }

}