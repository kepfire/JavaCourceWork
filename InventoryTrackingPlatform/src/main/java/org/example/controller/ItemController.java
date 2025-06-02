package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.domain.ItemDTO;
import org.example.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@Tag(name = "Items", description = "APIs for managing inventory items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    @Operation(summary = "Get all items", description = "Retrieves a list of items with optional filtering and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved items"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public List<ItemDTO> getAllItems(
            @Parameter(description = "Filter by category") @RequestParam(required = false) String category,
            @Parameter(description = "Filter by supplier") @RequestParam(required = false) String supplier,
            @Parameter(description = "Sort by field (name, category, currentQuantity)") @RequestParam(required = false) String sortBy) {
        return itemService.findAllItems(category, supplier, sortBy);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID", description = "Retrieves a specific item by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved item"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ItemDTO getItemById(@Parameter(description = "Item ID") @PathVariable UUID id) {
        return itemService.findById(id);
    }

    @GetMapping("/critical")
    @Operation(summary = "Get critical items", description = "Retrieves items with quantity below critical level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved critical items"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public List<ItemDTO> getCriticalItems() {
        return itemService.findCriticalItems();
    }

    @PostMapping
    @Operation(summary = "Create new item", description = "Creates a new inventory item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created item"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ItemDTO addItem(@Parameter(description = "Item details") @RequestBody ItemDTO itemDTO) {
        return itemService.createItem(itemDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update item", description = "Updates an existing inventory item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated item"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ItemDTO updateItem(
            @Parameter(description = "Item ID") @PathVariable UUID id,
            @Parameter(description = "Updated item details") @RequestBody ItemDTO itemDTO) {
        return itemService.updateItem(id, itemDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item", description = "Deletes an inventory item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted item"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteItem(@Parameter(description = "Item ID") @PathVariable UUID id) {
        itemService.deleteItem(id);
    }
}