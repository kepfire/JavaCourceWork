package org.example.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperationSource {
    MANUAL("Ручне"),
    ERP("ERP система"),
    API("API інтеграція"),
    SCANNER("Сканер штрих-кодів"),
    IMPORT("Імпорт даних");

    private final String displayName;

}