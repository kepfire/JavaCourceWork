package org.example.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperationType {
    INCOMING("Прихід"),
    OUTGOING("Видача"),
    CORRECTION("Корекція"),
    WRITE_OFF("Списання"),
    INVENTORY("Інвентаризація");

    private final String displayName;

}