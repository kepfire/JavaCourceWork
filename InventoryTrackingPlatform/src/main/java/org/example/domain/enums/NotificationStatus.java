package org.example.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationStatus {
    UNREAD("unread"),
    READ("read"),
    ARCHIVED("archived");

    private final String status;

}
