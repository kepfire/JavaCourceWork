package org.example.service;

import org.example.domain.ItemDTO;
import java.util.List;
import java.util.UUID;

public interface ItemService {
    List<ItemDTO> findAllItems(String category, String supplier, String sortBy);
    ItemDTO findById(UUID id);
    List<ItemDTO> findCriticalItems();
    ItemDTO createItem(ItemDTO itemDTO);
    ItemDTO updateItem(UUID id, ItemDTO itemDTO);
    void deleteItem(UUID id);
    void checkStockLevels();
}