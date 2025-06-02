package org.example.domain;

import lombok.Value;
import lombok.Builder;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class ItemDTO {
    UUID itemId;
    String name;
    String category;
    int currentQuantity;
    Integer criticalQuantity;
    String supplier;
    String description;
    String barcode;
}